/***
 * JAVANAISE Implementation
 * JvnCoordImpl class
 * This class implements the Javanaise central coordinator
 * Contact:
 *
 * Authors: MathysC MatveiP
 */

package jvn.coord;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jvn.object.JvnObject;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;
import java.io.Serializable;

public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {

	/**
	 * Serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Incremental Identification Number generator.
	 */
	private int idGenerator = 0;

	// cache works with id-object
	/**
	 * Link the name of an object to its ID.
	 */
	private Map<String, Integer> nameMap = new HashMap<>();

	/**
	 * Link the ID of an object to its instance.
	 */
	private Map<Integer, JvnObject> objectMap = new HashMap<>();

	/**
	 * Link the ID of an object to its RemoteServer as a Writer.
	 */
	private transient Map<Integer, JvnRemoteServer> writerMap = new HashMap<>();

	/**
	 * Link the ID of an object to the RemoteServers as Readers.
	 */
	private transient Map<Integer, ArrayList<JvnRemoteServer>> readerMap = new HashMap<>();

	// Registry for communication
	public static final String COORD_NAME = "coordinator";
	public static final int COORD_PORT = 2001;
	public static final String COORD_HOST = "127.0.1.1";
	public static final String PROPERTY = "java.rmi.server.hostname";

	/**
	 * Default constructor
	 * 
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 */
	private JvnCoordImpl() throws RemoteException, AlreadyBoundException {
		Registry registry = LocateRegistry.createRegistry(COORD_PORT);
		registry.bind(COORD_NAME, this);
	}

	public static void main(String[] args) {
		try {
			// Créer le serveur
			System.setProperty(PROPERTY, COORD_HOST);
			JvnCoordImpl coord = new JvnCoordImpl();
			if (coord.isReady()) {
				System.out.println("Server ready");
			} else {
				throw new JvnException(JvnCoordImpl.class.getName() + " is not ready");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * A coordinator is ready if there isn’t a value in the coordinator registry.
	 * 
	 * @return true if no value is found at the coordinator's registry
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public synchronized boolean isReady() throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry(COORD_PORT);
		return registry.lookup(COORD_NAME) != null;
	}

	@Override
	public synchronized int jvnGetObjectId() throws RemoteException, jvn.utils.JvnException {
		return ++this.idGenerator;
	}

	@Override
	public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException {
		int id = jo.jvnGetObjectId();

		this.nameMap.put(jon, id);
		this.objectMap.put(id, jo);
		this.writerMap.put(id, js);
		this.readerMap.put(id, new ArrayList<>());
	}

	@Override
	public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException {
		return this.objectMap.get(this.nameMap.get(jon));
	}

	@Override
	public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		JvnObject jo = this.objectMap.get(joi);
		Serializable serializable = jo.jvnGetSharedObject();
		JvnRemoteServer writer = this.writerMap.get(joi);

		// if object is locked in W
		if (writer != null && !writer.equals(js)) {
			serializable = writer.jvnInvalidateWriterForReader(joi);
			this.writerMap.put(joi, null);
			/**
			 * If the writer is not the one calling jvnLockread function
			 * add it to the list of reader
			 */
			this.readerMap.get(joi).add(writer);

			// Change saved value
			jo.jvnSetSharedObject(serializable);
		}

		this.readerMap.get(joi).add(js);
		return serializable;
	}

	@Override
	public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException {
		JvnObject jo = this.objectMap.get(joi);
		Serializable serializable = jo.jvnGetSharedObject();
		JvnRemoteServer writer = this.writerMap.get(joi);

		// Case Write
		if (writer != null && (!writer.equals(js))) {
			// Invalidate writer
			serializable = writer.jvnInvalidateWriter(joi);
			jo.jvnSetSharedObject(serializable);

		}

		// Invalidate readers
		for (JvnRemoteServer reader : this.readerMap.get(joi)) {
			if (!reader.equals(js))
				reader.jvnInvalidateReader(joi);
		}

		this.readerMap.get(joi).clear();
		this.writerMap.put(joi, js);
		return serializable;
	}

	@Override
	public synchronized void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException {
		for (var entry : this.writerMap.entrySet()) {
			JvnRemoteServer writer = entry.getValue();
			if (writer != null && (writer.equals(js))) {
				int joi = entry.getKey();
				JvnObject jo = this.objectMap.get(joi);
				Serializable serializable = writer.jvnInvalidateWriter(joi);
				jo.jvnSetSharedObject(serializable);
				this.writerMap.put(joi, null);
			}
		}

		this.readerMap.forEach((id, readers) -> readers.remove(js));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + idGenerator;
		result = prime * result + ((nameMap == null) ? 0 : nameMap.hashCode());
		result = prime * result + ((objectMap == null) ? 0 : objectMap.hashCode());
		result = prime * result + ((writerMap == null) ? 0 : writerMap.hashCode());
		result = prime * result + ((readerMap == null) ? 0 : readerMap.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		JvnCoordImpl other = (JvnCoordImpl) obj;
		if (idGenerator != other.idGenerator)
			return false;
		if (nameMap == null) {
			if (other.nameMap != null)
				return false;
		} else if (!nameMap.equals(other.nameMap))
			return false;
		if (objectMap == null) {
			if (other.objectMap != null)
				return false;
		} else if (!objectMap.equals(other.objectMap))
			return false;
		if (writerMap == null) {
			if (other.writerMap != null)
				return false;
		} else if (!writerMap.equals(other.writerMap))
			return false;
		if (readerMap == null) {
			if (other.readerMap != null)
				return false;
		} else if (!readerMap.equals(other.readerMap))
			return false;
		return true;
	}
}
