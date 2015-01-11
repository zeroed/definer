/**
 * 
 */
package org.code.definer;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.codehaus.jettison.json.JSONObject;

/**
 * @author eddie
 *
 */
public class Definitions {
	private String[] definitions;
	private static final String STORAGE_FILE = "";
	
	public Definitions(String[] definitions) {
		this.definitions = definitions;
	}
	
	public String[] getDefinitions() {
		return definitions;
	}
	
	public boolean persistOnFile() {
		try {
			new Parser("/home/eddie/Downloads/words.json", 
					getWords());
			// TODO: this return is blasphemy
			return true;
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} 
	}
	
	public boolean addToWords() {
		return false;
	}
	
	private List<Word> getWords() {
		return Arrays.stream(definitions)
				.map(s -> {
					JSONObject w;
					try {
						w = new JSONObject(s);
					String name = (String) w.keys().next();
					return new Word(name, (String) w.get(name));
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}).collect(Collectors.toList());
	}
	
	@Override
	public String toString() {
		return Arrays.stream(definitions)
				.map(s -> {
					try {
						return new JSONObject(s).toString(2);
					} catch (Exception e) {
						return String.format(
								"{\"ERROR\":\"error printing %s\"}", s);
					}
				})
				.collect(Collectors.joining("\n"));
	}
}