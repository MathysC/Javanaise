/***
 * Sentence class : used for keeping the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package irc;

/**
 * Sentence is a Serializable object that will be used as a shareable object by
 * Javanaise.
 */
public class Sentence implements ISentence {

	/**
	 * Serialization.
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * The value contained by the Sentence.
	 */
	String data;

	public Sentence() {
		data = "";
	}

	/**
	 * Setter of data.
	 * 
	 * @param text The new value of data.
	 */
	public void write(String text) {
		data = text;
	}

	/**
	 * Getter of data.
	 * 
	 * @return the current value of data.
	 */
	public String read() {
		return data;
	}

}