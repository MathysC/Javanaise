/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:
 *
 * Authors:
 */

package jvn.coord;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import jvn.object.JvnObject;
import jvn.object.JvnObjectImpl;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;

import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	// cache works with id-object
	private Map<Integer, JvnObject> cache;

	// Registry for communication
	private Registry registry;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 */
	private JvnCoordImpl() throws Exception {
		// to be completed
		registry = LocateRegistry.getRegistry(2001);
		registry.bind("coordinator", this);
		cache = new HashMap<>();
	}

	/**
	 * Allocate a NEW JVN object id (usually allocated to a
	 * newly created JVN object)
	 * 
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public int jvnGetObjectId()
			throws java.rmi.RemoteException, jvn.utils.JvnException {
		JvnObject jo = new JvnObjectImpl();
		int id = 0; // TODO
		cache.put(id, jo);
		return id;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.utils.JvnException {
		// to be completed
		// .bind(jon, jon)
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server
	 * 
	 * @param jon : the JVN object name
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException, jvn.utils.JvnException {
		// to be completed
		return null;
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		// TODO to be completed
		return this.cache.get(joi);
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		// to be completed
		return this.cache.get(joi);
	}

	/**
	 * A JVN server terminates
	 * 
	 * @param js : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		// TODO ask if when coord terminates, all the servers should terminate
		try {
			registry.unbind("coordinator");
		} catch (RemoteException | NotBoundException e) {
			// Couldn't unbind, so already unbound.
			// Do nothing, objective already achieved.
		}
	}
}
