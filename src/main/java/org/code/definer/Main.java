/**
 * 
 */
package org.code.definer;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author eddie
 *
 */
public class Main {

	private static final Logger logger = LogManager.getLogger(Main.class);
	private final static String QUERY_URL_PATTERN = "http://"
			+ "dictionary.reference.com/"
			+ "browse/"
			+ "%s?s=t";
	private final static String CSS_DEFINITION_LIST = "def-list";
	private final static String CSS_DEFINITION_SET = "def-set";
	private final static String NOT_FOUND = "There aren't definitions for your word. Perhaps...";

	private String[] words;
	
	private Main(String[] words) {
		this.words = sanitizeArgs(words);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			try {
				Definitions definitions = new Definitions(
						new Main(args).getDefinitions());
				logger.info("\n");
				logger.info(definitions);
				logger.info("\n ~ ");
			} catch (Exception e) {
				logger.info(String.format("Unable to retrieve your definition: %s.",
						e.getMessage()));
			}
		} else {
			printUsageAndExit(args);
		}
	}

	private static void printUsageAndExit(String[] args) {
		if (args.length == 0) {
			logger.info(String.format("Missing word!"));
		} else {
			logger.info(String.format("Too many parameters: %s",
					Arrays.stream(args)
					.map(Object::toString)
					.collect(Collectors.joining(", "))));
		}
		logger.info(String.format("Usage: definer <word>"));
		System.exit(1);
	}

	private String[] getDefinitions() throws IOException {
		return Arrays.stream(words).map(w -> {
			try {
				return getDefinition(w);
			} catch (IOException e) {
				logger.error(String.format("Error defining %s", w));
				return null;
			}
		}).toArray(String[]::new);
	}
	
	private String getDefinition(String word) throws IOException {
		logger.trace(String.format("Retrieving definition of \"%s\"...", word));
		Document doc = null;
		try {
			doc = Jsoup.connect(String.format(QUERY_URL_PATTERN, word)).get();
			Elements elements = doc.getElementsByClass(CSS_DEFINITION_LIST);
			if (elements.size() > 0) {
				Elements definitions = elements.get(0)
						.getElementsByClass(CSS_DEFINITION_SET);
				logger.info(String.format("There are %d definition(s) for \"%s\".", 
						definitions.size(), 
						word));
				logger.trace(String.format(
						definitions.stream()
						.map(Element::text)
						.collect(Collectors.joining("\n"))));
				return makeJsonFromDefinition(word, definitions.text());
			} else {
				return NOT_FOUND;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	private String makeJsonFromDefinition(String word, String definition) {
		try {
			return new JSONObject().put(word, definition).toString();
		} catch (JSONException e) {
			return String.format("{\"ERROR\":\"error parsing definition for %s\"}", word);
		}
	}
	
	private String[] sanitizeArgs(String[] args) {
		return Arrays.
				stream(args)
				.filter(s -> s.length() > 0)
				.map(s -> {
					return s.replaceAll("[^a-zA-Z]", "");
				})
				.toArray(String[]::new);
	}
	
	static class Definitions {
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
}
