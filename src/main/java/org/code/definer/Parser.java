package org.code.definer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Stack;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.xml.sax.InputSource;


public class Parser {

	public Parser(String filename) throws FileNotFoundException, UnsupportedEncodingException {

		File file = new File(filename);
		FileInputStream inputStream = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(inputStream,"UTF-8");
		InputSource is = new InputSource(reader);
		is.setEncoding("UTF-8");

		try (JsonParser parser = Json.createParser(inputStream)) {
			
			Stack<String> currentObjects = new Stack<String>();
			Stack<String> currentKeys = new Stack<String>();
			Stack<String> currentArrays = new Stack<String>();
			currentObjects.push("root");
			
			while (parser.hasNext()) {
				Event event = parser.next();
				System.out.printf("On %s event the current object stacks is %s.\n", 
						event.name().toLowerCase(),
						currentObjects);
				
				switch (event) {
				case START_OBJECT: {
					break;
				}
				case END_OBJECT: {
					if (currentArrays.isEmpty()) {
						currentObjects.pop();
					} else {
						System.out.printf("Skip closing because in %s...\n", currentArrays.peek());
					}
					break;
				}
				case START_ARRAY: {
					currentArrays.push(currentObjects.peek());
					System.out.printf("entering the Array (%s)...\n", currentArrays.peek());
					break;
				}
				case END_ARRAY: {
					currentObjects.pop();
					String endingArray = currentArrays.pop();
					System.out.printf("exiting the Array (%s)...\n", endingArray);
					break;
				}
				case KEY_NAME: {
					currentObjects.push(parser.getString());
					System.out.printf("adding %s...\n", currentObjects.peek());
					
					switch (currentObjects.peek()) {
					case "words":
						System.out.printf("Words founded. (%s)\n", parser.getString());
						break;
					case "config":
						System.out.printf("Config founded. (%s)\n", parser.getString());
						break;
					default:
						System.out.printf(" - %s: ", parser.getString()); 
					}
					break;
				}
				case VALUE_STRING: {
					currentObjects.pop();
					System.out.printf("%s\n", parser.getString());
					break;
				}
				default:
					event.name();
					break;
				}

			}
		} 
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

	private void eventuallyAddTheNewWord(String currentKey) {
		final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;
		int comparison = currentKey.compareToIgnoreCase(newWord);
		System.out.printf("The current keys is %s and the new word is %s = %d\n", currentKey, newWord, comparison);
		if (comparison < 0) {
			System.out.printf("%s precedes %s.\n", currentKey, newWord);
		} else if (comparison > 0) {
			System.out.printf("%s follows %s.\n", currentKey, newWord);
		} else {
			System.out.printf("%s equal %s.\n", currentKey, newWord);
		}
	}
}
