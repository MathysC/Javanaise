/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;

public class JvnServerImpl
		extends UnicastRemoteObject
		implements JvnLocalServer, JvnRemoteServer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// A JVN server is managed as a singleton
	private static JvnServerImpl js = null;
	private JvnRemoteCoord coordinator;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		Registry reg = LocateRegistry.getRegistry(2001);
		coordinator = (JvnRemoteCoord) reg.lookup("coordinator");
		reg.bind("srv_" + JvnServerImpl.serialVersionUID, this);
	}

	/**
	 * Static method allowing an application to get a reference to
	 * a JVN server instance
	 * 
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null) {
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 * 
	 * @throws JvnException
	 **/
	public void jvnTerminate()
			throws jvn.JvnException, RemoteException {
		this.coordinator.jvnTerminate(this);
		UnicastRemoteObject.unexportObject(this, false);

	}

	/**
	 * creation of a JVN object
	 * 
	 * @param o : the JVN object state
	 * @throws JvnException
	 * @throws RemoteException
	 **/
	public JvnObject jvnCreateObject(Serializable o)
			throws jvn.JvnException, RemoteException {

		JvnObject jo = (JvnObject) o;

		coordinator.jvnRegisterObject("cool name", jo, this);
		// to be completed
		return jo;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo)
			throws jvn.JvnException, RemoteException {
		this.coordinator.jvnRegisterObject(jon, jo, this);
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * 
	 * @param jon : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon)
			throws jvn.JvnException, RemoteException {
		return this.coordinator.jvnLookupObject(jon, this);
	}

	/**
	 * Get a Read lock on a JVN object
	 * 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi)
			throws JvnException {
		// to be completed
		return null;

	}

	/**
	 * Get a Write lock on a JVN object
	 * 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi)
			throws JvnException {
		// to be completed
		return null;
	}

	/**
	 * Invalidate the Read lock of the JVN object identified by id
	 * called by the JvnCoord
	 * 
	 * @param joi : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi)
			throws java.rmi.RemoteException, jvn.JvnException {
		// to be completed
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 * 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi)
			throws java.rmi.RemoteException, jvn.JvnException {
		// to be completed
		return null;
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id
	 * 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi)
			throws java.rmi.RemoteException, jvn.JvnException {
		// to be completed
		return null;
	};

}
