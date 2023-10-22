/***
 * JAVANAISE API
 * JvnLocalServer interface
 * Defines the local interface provided by a JVN server 
 * An application uses the Javanaise service through the local interface provided by the Jvn server 
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package jvn.server;

import java.io.Serializable;
import java.rmi.RemoteException;

import jvn.object.JvnObject;
import jvn.utils.JvnException;

/**
 * Local interface of a JVN server (used by the applications).
 * An application can get the reference of a JVN server through the static
 * method jvnGetServer() (see JvnServerImpl).
 */

public interface JvnLocalServer {

	/**
	 * Create of a JVN object.
	 * 
	 * @param jos : the JVN object state
	 * @return the JVN object
	 * @throws JvnException
	 * @throws RemoteException
	 **/
	public Object jvnCreateObject(Serializable jos) throws JvnException, RemoteException;

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @throws JvnException
	 * @throws RemoteException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo) throws JvnException, RemoteException;

	/**
	 * Get the reference of a JVN object associated to a symbolic name
	 * 
	 * @param jon : the JVN object symbolic name
	 * @return the JVN object
	 * @throws JvnException
	 * @throws RemoteException
	 **/
	public Object jvnLookupObject(String jon) throws JvnException, RemoteException;

	/**
	 * Get a Read lock on a JVN object
	 * 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException;

	/**
	 * Get a Write lock on a JVN object
	 * 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException;

	/**
	 * The JVN service is not used anymore by the application
	 * 
	 * @throws JvnException
	 * @throws RemoteException
	 **/
	public void jvnTerminate() throws JvnException, RemoteException;
}
