package org.code.definer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Stack;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;
import javax.json.stream.JsonParsingException;

import org.xml.sax.InputSource;


public class Parser {

	private String filename;
	private Word newWord;
	private LinkedList<Word> words;
	private boolean notYetAdded = true;

	public Parser(String filename, Word newWord) throws FileNotFoundException, UnsupportedEncodingException {

		this.filename = filename;
		this.newWord = newWord;
		
		System.out.printf("loading %s...\n", this.filename);
		File file = new File(filename);
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
			String currentName;
			String currentDefinition;
			currentName = currentDefinition = null;
			boolean readingWords = false;
			boolean readingConfig = false;

			while (parser.hasNext()) {
				Event event = goNext(parser);

				// System.out.printf("On %s event the current object stacks is %s.\n", event.name().toLowerCase(), currentObjects);

				switch (event) {
					case START_OBJECT: {
						if (!currentArrays.isEmpty() && "words".equalsIgnoreCase(currentArrays.peek())) {
							// Start loading a word...
						}
						break;
					}
					case END_OBJECT: {
						if (currentArrays.isEmpty()) {
							currentObjects.pop();
						} else {
							// System.out.printf("Skip closing because in %s...\n", currentArrays.peek());
							if ("words".equalsIgnoreCase(currentArrays.peek())) {
								loadWord(currentName, currentDefinition);
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
							if (readingWords) currentName = parser.getString();
							System.out.printf(" - %s: ", parser.getString()); 
						}
						break;
					}
					case VALUE_STRING: {
						if (readingWords) currentDefinition = parser.getString();
						System.out.printf("%s\n", parser.getString());
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
		System.out.printf("Words: %s", words);
	}

	/**
	 * OMG!
	 * Refactor this crap...
	 * 
	 * @param parser
	 * @return
	 */
	private Event goNext(JsonParser parser) {
		try {
			return parser.next();
		} catch (JsonParsingException parsingException) {
			if (parsingException.getMessage().contains("Invalid token=CURLYCLOSE")) {
				System.out.printf("Handled a trailing comma ending on multiple... Invalid token=CURLYCLOSE\n");
				return goNext(parser);
			} else if (parsingException.getMessage().contains("Invalid token=CURLYOPEN")) {
				System.out.printf("Handled a trailing comma ending on multiple... Invalid token=CURLYOPEN\n");
				return goNext(parser);
			} else if (parsingException.getMessage().contains("Invalid token=SQUARECLOSE")) {
				System.out.printf("Handled a trailing comma ending on array... Invalid token=SQUARECLOSE\n");
				return goNext(parser);
			} else if (parsingException.getMessage().contains("Invalid token=SQUAREOPEN")) {
				System.out.printf("Handled a trailing comma ending on array... Invalid token=SQUAREOPEN\n");
				return goNext(parser);
			} else if (parsingException.getMessage().contains("Invalid token=COLON")) {
				System.out.printf("Handled a trailing comma ending on array... Invalid token=COLON\n");
			} else {
				throw parsingException;
			}
			return goNext(parser);
		}
	}

	private void archiveNewWord() {
		words.add(newWord);
		notYetAdded = false;
	}
	
	private void loadWord(String name, String definition) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		int comparison = name.compareToIgnoreCase(newWord.getName());
		if (comparison < 0) {
			// System.out.printf("%s precedes %s.\n", name, newWord.getName());
		} else if (comparison > 0) {
			// System.out.printf("%s follows %s.\n", name, newWord.getName());
			if (notYetAdded) archiveNewWord();
		} else {
			// System.out.printf("%s equal %s.\n", name, newWord.getName());
		}
		words.add(new Word(name, definition));
	}
}
