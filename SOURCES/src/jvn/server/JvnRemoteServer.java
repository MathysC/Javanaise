/***
 * JAVANAISE API
 * JvnRemoteServer interface
 * Defines the remote interface provided by a JVN server 
 * This interface is intended to be invoked by the Javanaise coordinator 
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package jvn.server;

import java.rmi.*;

import jvn.utils.JvnException;

import java.io.*;

/**
 * Remote interface of a JVN server (used by a remote JvnCoord)
 */
public interface JvnRemoteServer extends Remote {

	/**
	 * Invalidate the Read lock of the JVN object identified by id
	 * called by the JvnCoord
	 * 
	 * @param joi : the JVN object id
	 * @return void
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public void jvnInvalidateReader(int joi) throws RemoteException, jvn.utils.JvnException;

	/**
	 * Invalidate the Write lock of the JVN object identified by id
	 * 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi) throws RemoteException, JvnException;

	/**
	 * Reduce the Write lock of a JVN object
	 * 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws RemoteException
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi) throws RemoteException, JvnException;

}
