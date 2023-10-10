/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Implementation of a Jvn server
 * Contact: 
 *
 * Authors: 
 */

package jvn.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import jvn.coord.JvnCoordImpl;
import jvn.coord.JvnRemoteCoord;
import jvn.object.JvnObject;
import jvn.object.JvnObjectImpl;
import jvn.utils.JvnException;

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
	
	private int serverID = -1; 
	
	private Registry reg;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		System.setProperty("java.rmi.server.hostname","127.0.1.1");
		this.reg = LocateRegistry.getRegistry(2001);
		coordinator = (JvnRemoteCoord) reg.lookup("coordinator");
		this.serverID = coordinator.jvnGetNextServerId();
		this.coordinator.log(this.serverID+" Registered");
		reg.bind("srv_" + this.serverID, this);
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
				e.printStackTrace();
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
			throws jvn.utils.JvnException, RemoteException {
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
			throws jvn.utils.JvnException, RemoteException {
		int id = 0; 
		try {
			id = this.coordinator.jvnGetObjectId();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JvnObject jo = new JvnObjectImpl(o,id, this);	

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
			throws jvn.utils.JvnException, RemoteException {
		try {
			this.coordinator.jvnRegisterObject(jon, jo, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * 
	 * @param jon : the JVN object name
	 * @return the JVN object
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon)
			throws jvn.utils.JvnException, RemoteException {
		JvnObject object = this.coordinator.jvnLookupObject(jon, this);
		if (object != null) {
			// change server to be current instead of creator
			object.jvnSetServer(this);
		}
		return object;
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
		try {
			return this.coordinator.jvnLockRead(joi, this);
		} catch (RemoteException | JvnException e) {
			e.printStackTrace();
			return null;
		}
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
		try {
			return this.coordinator.jvnLockWrite(joi, this);
		} catch (RemoteException | JvnException e) {
			e.printStackTrace();
			return null;
		}
		
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
			throws java.rmi.RemoteException, jvn.utils.JvnException {
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
			throws java.rmi.RemoteException, jvn.utils.JvnException {
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
			throws java.rmi.RemoteException, jvn.utils.JvnException {
		// to be completed
		return null;
	};

}
