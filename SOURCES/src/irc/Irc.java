/***
 * Irc class : simple implementation of a chat using JAVANAISE 
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package irc;

import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

import jvn.object.JvnObject;
import jvn.server.JvnServerImpl;
import jvn.utils.JvnException;

public class Irc {
	protected TextArea text;
	protected TextField data;
	Frame frame;
	ISentence sentence;

	/**
	 * main method
	 * create a JVN object nammed IRC for representing the Chat application
	 **/
	public static void main(String[] argv) {
		try {

			// initialize JVN
			JvnServerImpl js = JvnServerImpl.jvnGetServer();

			// look up the IRC object in the JVN server
			// if not found, create it, and register it in the JVN server
			ISentence jo = (ISentence)js.jvnLookupObject("IRC", new Sentence());
			// create the graphical part of the Chat application
			new Irc(jo);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IRC problem : " + e.getMessage());
		}
	}

	/**
	 * IRC Constructor
	 * 
	 * @param jo the JVN object representing the Chat
	 **/
	public Irc(ISentence jo) {
		sentence = jo;
		frame = new Frame();
		frame.setLayout(new GridLayout(1, 1));
		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data = new TextField(40);
		frame.add(data);
		Button readButton = new Button("read");
		readButton.addActionListener(new ReadListener(this));
		frame.add(readButton);
		Button writeButton = new Button("write");
		writeButton.addActionListener(new WriteListener(this));
		frame.add(writeButton);
		frame.setSize(545, 201);
		text.setBackground(Color.black);
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					JvnServerImpl.jvnGetServer().jvnTerminate();
				} catch (RemoteException | JvnException e1) {
					e1.printStackTrace();
				} finally {
					e.getWindow().dispose();
				}
			}
		});
	}
}

/**
 * Internal class to manage user events (read) on the CHAT application
 **/
class ReadListener implements ActionListener {
	Irc irc;

	public ReadListener(Irc i) {
		irc = i;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed(ActionEvent e) {
		// invoke the method
		String s = irc.sentence.read();

		// unlock the object
		//irc.sentence.jvnUnLock();

		// display the read value
		irc.data.setText(s);
		irc.text.append(s + "\n");
	}
}

/**
 * Internal class to manage user events (write) on the CHAT application
 **/
class WriteListener implements ActionListener {
	Irc irc;

	public WriteListener(Irc i) {
		irc = i;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed(ActionEvent e) {
		// get the value to be written from the buffer
		String s = irc.data.getText();

		// lock the object in write mode
		//irc.sentence.jvnLockWrite();

		// invoke the method
		irc.sentence.write(s);
	}
}
