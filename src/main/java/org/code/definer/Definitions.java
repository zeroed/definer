/**
 * 
 */
package org.code.definer;

import java.util.Arrays;
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
	
	public void persistOnFile() {
		//TODO: implementme!
	}
	
	public boolean addToWords() {
		return false;
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