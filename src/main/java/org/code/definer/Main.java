/**
 * 
 */
package org.code.definer;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	private String word;
	
	private Main(String word) {
		this.word = word;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 1 && args[0].length() > 0) {
			try {
				new Main(args[0]).getDefinition();
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

	private String getDefinition() throws IOException {
		logger.info(String.format("Retrieving definition of \"%s\"...", word));
		Document doc = null;
		try {
			doc = Jsoup.connect(String.format(QUERY_URL_PATTERN, word)).get();
			Elements elements = doc.getElementsByClass(CSS_DEFINITION_LIST);
			if (elements.size() > 0) {
				Elements definitions = elements.get(0)
						.getElementsByClass(CSS_DEFINITION_SET);
				logger.info(String.format("There are %d definition(s) for %s:", 
						definitions.size(), 
						word));
				logger.info(String.format(
						definitions.stream()
						.map(Element::text)
						.collect(Collectors.joining("\n"))));
				return definitions.text();
			} else {
				return NOT_FOUND;
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
}
