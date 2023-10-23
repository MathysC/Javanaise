/***
 * Irc class : simple implementation of a chat using JAVANAISE 
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package irc;

import java.text.DecimalFormat;
import java.util.Random;

import jvn.server.JvnServerImpl;

public class IrcHeadless {
	ISentence sentence;
	/**
	 * main method
	 * create a JVN object nammed IRC for representing the Chat application
	 **/
	public static void main(String[] argv) {
		try {
			
			int nbActions = 50000;

			// initialize JVN
			JvnServerImpl js = JvnServerImpl.jvnGetServer();
			// look up the IRC object in the JVN server
			// if not found, create it, and register it in the JVN server
			ISentence jo = (ISentence)js.jvnLookupObject("IRC", new Sentence());
			// create the graphical part of the Chat application
			Random random = new Random();
			int read = 0;
			int write = 0;
			for (int i = 0; i<nbActions; i++) {
				if (random.nextBoolean()) {
					jo.read();
					read++;
				} else {
					String s =  new DecimalFormat("#.###").format(random.nextDouble());;
					jo.write(s);
					write++;
				}
			}
			System.out.println("Irc headless safely ended after "+read+" reads and "+write+" writes");
			

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IRC problem : " + e.getMessage());
		}
	}
}

