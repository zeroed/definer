package org.code.definer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.xml.sax.InputSource;


public class Parser {

	private String filename;
	private LinkedList<Word> words;
	private HashMap<String, String> config;

	public Parser(String filename, List<Word> newWords) throws FileNotFoundException, UnsupportedEncodingException {

		this.filename = filename;
		System.out.printf("loading %s...\n", this.filename);
		File file = new File(filename);
		if (!file.exists()) {
			throw new RuntimeException(String.format("File %s missing!", this.filename));
		}
		FileInputStream inputStream = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");

		try (JsonParser parser = Json.createParser(inputStream)) {

			String currentKey = null;
			Stack<String> currentObjects = new Stack<String>();
			Stack<String> currentArrays = new Stack<String>();
			currentObjects.push("root");
			words = new LinkedList<Word>();
			config = new HashMap<String, String>();
			String currentName;
			String currentDefinition;
			currentName = currentDefinition = null;
			boolean readingWords = false;
			boolean readingConfig = false;

			while (parser.hasNext()) {
				Event event = parser.next();

				switch (event) {
					case START_OBJECT: {
						if (!currentArrays.isEmpty() && "words".equalsIgnoreCase(currentArrays.peek())) {
							// Start loading a word...
						}
						break;
					}
					case END_OBJECT: {
						if (readingConfig) {
							readingConfig = false;
							System.out.printf("Config done: %s.\n", config);
						}

						if (currentArrays.isEmpty()) {
							currentObjects.pop();
						} else {
							if ("words".equalsIgnoreCase(currentArrays.peek())) {
								words.add(new Word(currentName, currentDefinition));
								currentName = currentDefinition = null;
							}
						}
						break;
					}
					case START_ARRAY: {
						currentArrays.push(currentObjects.peek());
						if ("words".equalsIgnoreCase(currentArrays.peek())) {
							readingWords = true;
						}
						break;
					}
					case END_ARRAY: {
						currentObjects.pop();
						String endingArray = currentArrays.pop();
						if ("words".equalsIgnoreCase(endingArray)) {
							readingWords = false;
							words.addAll(newWords);
							words.sort(new Comparator<Word>() {
								    public int compare(Word lhs, Word rhs) {  
								      return lhs.getName().compareTo(rhs.getName());  
								    }
								});
							System.out.printf("Loaded %d words.\n", words.size());
						}
						break;
					}
					case KEY_NAME: {
						currentKey = currentObjects.push(parser.getString());
						switch (currentObjects.peek()) {
						case "words":
							System.out.printf("loading Words (%s).\n", currentKey);
							break;
						case "config":
							System.out.printf("loading Config (%s).\n", currentKey);
							readingConfig = true;
							break;
						default:
							currentName = parser.getString();
							System.out.printf(" > %s: ", currentName); 
						}
						break;
					}
					case VALUE_STRING: {
						currentDefinition = parser.getString();
						System.out.printf("%s\n", currentDefinition);
						if (readingConfig) config.put(currentName, currentDefinition);
						currentObjects.pop();
						break;
					}
					default: {
						event.name();
						break;
					}
				}
			}
		}
		System.out.printf("Words: %s\n", words);
		new Writer(words, config).write();
	}
}
