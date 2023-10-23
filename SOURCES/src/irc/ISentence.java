/***
 * ISentence Interface : 
 * This interface defines invokable method used by InvocationHandler.
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */
package irc;

import java.io.Serializable;

import jvn.annotations.Operation;

/**
 * Sentence Interface.
 * 
 * @implNote Can't cast on Sentence, ISentence is thus necessary
 */
public interface ISentence extends Serializable {

	/**
	 * Read the value.
	 * 
	 * @return The updated value.
	 */
	@Operation(name = "read")
	String read();

	/**
	 * Write a new value.
	 * 
	 * @param s The new value.
	 */
	@Operation(name = "write")
	void write(String s);
}
