/**
 * 
 */
package org.code.definer;

/**
 * @author eddie
 *
 */
public class Word {

	private String name;
	private String definition;
	
	/**
	 * 
	 */
	public Word(String name, String definition) {
		this.name = name;
		this.definition = definition;
	}

	public String getName() {
		return name;
	}

	public String getDefinition() {
		return definition;
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", name, definition);
	}
}
