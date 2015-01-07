package org.code.definer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
			while (parser.hasNext()) {
				Event event = parser.next();
				// System.out.println(event.name());
				switch (event) {
				case START_ARRAY: {
					System.out.printf("entering the Array...");
					break;
				}
				case KEY_NAME: {
					// if ("words".equalsIgnoreCase(event.name())) {
						System.out.printf(" - %s: ", parser.getString()); 
//					} else {
//						System.out.printf("Config %s:  ", parser.getString()); 
//					}
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
