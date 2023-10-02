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
import jvn.utils.LockState;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_NAME = "new object";

	private int idGenerator;
	// cache works with id-object
	private Map<String, Integer> nameMap; // Name linked to ID
	private Map<Integer, JvnObject> objectMap; // ID linked to object
	private Map<Integer, LockState> lockMap; // ID linked to state.
	
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
		this.nameMap = new HashMap<>();
		this.objectMap = new HashMap<>();
		this.lockMap = new HashMap<>();
		this.idGenerator = 0;
	}
	
	/**
	 * Incre
	 * @return
	 */
	private int getNextID() {
		// TODO ADD SEMAPHORE
		return ++this.idGenerator;
	}

	/**
	 * Allocate a NEW JVN object id (usually allocated to a
	 * newly created JVN object)
	 * 
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public int jvnGetObjectId()
			throws java.rmi.RemoteException, jvn.utils.JvnException {
		// Maybe add semaphore to this part.
		JvnObject jo = new JvnObjectImpl();
		int id = this.getNextID(); 						
		this.nameMap.put(JvnCoordImpl.DEFAULT_NAME, id);
		this.objectMap.put(id, jo);
		this.lockMap.put(id, LockState.NL);
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

		int id = this.jvnGetObjectId();
		// Change the name by removing and adding again the id.
		this.nameMap.remove(JvnCoordImpl.DEFAULT_NAME, id);
		this.nameMap.put(jon, id);
		this.objectMap.put(id, jo);


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
		return this.objectMap.get(this.nameMap.get(jon));
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
		return this.objectMap.get(joi);
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
		return this.objectMap.get(joi);
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
