/**
 * 
 */
package org.code.definer;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author eddie
 *
 */
public class Writer {

	private LinkedList<Word> wordList;
	private HashMap<String, String> configMap;
	
    public Writer(LinkedList<Word> wordList, HashMap<String, String> configMap) {
		super();
		this.wordList = wordList;
		this.configMap = configMap;
	}

	public boolean write() {
    
    	String filename = String.format(
    			"%s_%s.json",
    			configMap.get("file_location"),
    			LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
    			);
    	try {
			JSONObject config = new JSONObject();
			configMap.keySet()
				.stream()
				.forEach(key -> {
					try {
						config.put(key, configMap.get(key));
					} catch (Exception e) {
						// TODO Sir: you are ugly
						e.printStackTrace();
					}
				});
		 
			JSONArray words = new JSONArray();
			wordList.stream()
				.forEach(word -> {
					try {
						words.put(
								new JSONObject()
									.put(word.getName(), word.getDefinition()));
					} catch (Exception e) {
						// TODO Sir: you are ugly
						e.printStackTrace();
					}
				});
			
			JSONObject fileContentAsJson = new JSONObject()
				.put("words", words)
				.put("config", config);
	 
			FileWriter file = new FileWriter(filename);
			file.write(fileContentAsJson.toString());
			file.flush();
			file.close();
	 
			System.out.printf("written %s\n", filename);
			return true;
		} catch (IOException | JSONException e) {
			System.out.printf("Error writing file: %s", filename);
			e.printStackTrace();
			return false;
		}
     }
}
