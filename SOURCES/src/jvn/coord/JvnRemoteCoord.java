/***
 * JAVANAISE API
 * JvnRemoteCoord interface
 * This interface defines the remote interface provided by the Javanaise coordinator
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package jvn.coord;

import java.rmi.*;

import jvn.object.JvnObject;
import jvn.server.JvnRemoteServer;
import jvn.utils.JvnException;

import java.io.*;

/**
 * Remote Interface of the JVN Coordinator
 */

public interface JvnRemoteCoord extends Remote {

	/**
	 * Allocate a NEW JVN object id (usually allocated to a
	 * newly created JVN object)
	 * 
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public int jvnGetObjectId() throws RemoteException, JvnException;

	/**
	 * Associate a symbolic name with a JVN object
	 * 
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the JVNServer
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws RemoteException, JvnException;

	/**
	 * Get the reference of a JVN object managed by a given JVN server
	 * 
	 * @param jon : the JVN object name
	 * @param js  : the remote reference of the JVNServer
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws RemoteException, JvnException;

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi the JVN object identification
	 * @param js  the remote reference of the server
	 * @return the current JVN object state
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js) throws RemoteException, JvnException;

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server
	 * 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws RemoteException, JvnException;

	/**
	 * A JVN server terminates
	 * 
	 * @param js : the remote reference of the server
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js) throws RemoteException, JvnException;

	/**
	 * Write a log message in the Console
	 * 
	 * @param m The message to log
	 * @throws RemoteException
	 * @throws JvnException
	 */
	public void log(String m) throws RemoteException, JvnException;
}
