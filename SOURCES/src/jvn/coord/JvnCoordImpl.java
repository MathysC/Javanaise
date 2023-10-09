/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:
 *
 * Authors:
 */

package jvn.coord;

import java.rmi.AccessException;
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
	public static final String DEFAULT_JVN_OVJECT_NAME = "new object";

	private int idGenerator;
	private int serverIdGenerator;
	
	// cache works with id-object
	private Map<String, Integer> nameMap; // Name linked to ID
	private Map<Integer, JvnObject> objectMap; // ID linked to object
	private Map<Integer, LockState> lockMap; // ID linked to state.
	private Map<Integer, JvnRemoteServer> lockedServerOwner ; // ID linked to state.
	
	// Registry for communication
	private Registry registry;
	private static final String COORD_NAME = "coordinator";
	private static final int COORD_PORT = 2001;

	/**
	 * Default constructor
	 * 
	 * @throws JvnException
	 */
	private JvnCoordImpl() throws Exception {
		// to be completed
		try {
			this.registry = LocateRegistry.createRegistry(COORD_PORT);
			JvnCoordImpl coord = (JvnCoordImpl) this.registry.lookup(COORD_NAME);
		} catch (NotBoundException e) {
			this.nameMap = new HashMap<>();
			this.objectMap = new HashMap<>();
			this.lockMap = new HashMap<>();
			this.lockedServerOwner = new HashMap<>();
			this.idGenerator = 0;
			this.serverIdGenerator = 0;
			this.registry.bind(COORD_NAME, this);
		}
	}
	
	public static void main(String[] args) {
		try {
			// Créer le serveur
			System.setProperty("java.rmi.server.hostname","127.0.1.1");
			JvnCoordImpl coord = new JvnCoordImpl();
			if (coord.isReady()) {
				System.out.println("Server ready");
			} else {
				throw new JvnException("Coordinator not ready");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public boolean isReady() throws RemoteException, NotBoundException {
		return this.registry.lookup(COORD_NAME) != null;
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
	 * Give a server his id. Simple incrementation.
	 * TODO : improve this to keep track of the servers + Add semaphore
	 * @return a new id
	 */
	public int jvnGetNextServerId() {
		return ++this.serverIdGenerator;
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
		int id = this.getNextID(); 						
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
		int id = jo.jvnGetObjectId();
		this.nameMap.put(jon, id);
		this.objectMap.put(id, jo);
		this.lockMap.put(id, LockState.NL);

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
		this.log("Lock Read of "+joi);
		JvnObject obj = this.objectMap.get(joi);
		
		this.invalidateLocks(joi);
		
		this.lockMap.put(joi, LockState.R);
		this.lockedServerOwner.put(joi, js);
		this.log("Object is now locked in R");
		
		return obj.jvnGetSharedObject();
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
		this.log("Lock Write of "+joi);
		JvnObject obj = this.objectMap.get(joi);
		
		this.invalidateLocks(joi);
		
		this.lockedServerOwner.put(joi, js);
		this.lockMap.put(joi, LockState.W);
		return obj.jvnGetSharedObject();
	}
	
	private void invalidateLocks(int joi) throws RemoteException, JvnException {
		LockState currentLock = this.lockMap.get(joi);
		JvnRemoteServer owner = this.lockedServerOwner.get(joi);
		// check if object is currently locked
		switch (currentLock) {
			case NL:
				break;
			case R:
			case RC:
				owner.jvnInvalidateReader(joi);
				break;
			case W:
			case WC:
				owner.jvnInvalidateWriter(joi);
				break;
			default:
				throw new JvnException("Error: State is not instanciated yet");
		}
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
	
	public void log(String s) {
		System.out.println("[LOG] - "+s);
	}
}
