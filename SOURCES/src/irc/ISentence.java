package irc;

import java.io.Serializable;

import jvn.annotations.Operation;

// Can't cast on Sentence, ISentence is thus necessary
public interface ISentence extends Serializable{
	@Operation(name="read")
	String read();
	@Operation(name="write")
	void write(String s);
}
