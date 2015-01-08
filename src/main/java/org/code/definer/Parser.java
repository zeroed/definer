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

		try (
				JsonParser parser = Json.createParser(inputStream)) {
			Stack<String> currentObjects = new Stack<String>();
			Stack<String> currentArrays = new Stack<String>();
			while (parser.hasNext()) {
				Event event = parser.next();
//				System.out.printf("Current Object: %s.\n", currentObject);
				System.out.println(currentObjects);
				switch (event) {
				case START_OBJECT: {
					// System.out.printf("opening...\n");
					break;
				}
				case END_OBJECT: {
					System.out.printf("ending %s...\n", currentObjects.peek());
					currentObjects.pop();
					break;
				}
				case START_ARRAY: {
					currentArrays.push(currentObjects.peek());
					System.out.printf("entering the Array (%s)...\n", currentArrays.peek());
					break;
				}
				case END_ARRAY: {
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
//						System.out.printf("Config %s:  ", parser.getString()); 
					}
					break;
				}
				case VALUE_STRING: {
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


}
