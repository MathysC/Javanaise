/***
 * JAVANAISE API
 * JvnObject Interface
 * Define the interface of a JvnObject that will be shared between JvnServer, using the Javanaise Coordinator
 * Contact: 
 *
 * Authors: MathysC MatveiP
 */

package jvn.object;

import java.io.*;

import jvn.server.JvnLocalServer;
import jvn.utils.JvnException;

/**
 * Interface of a JVN object.
 * A JVN object is used to acquire read/write locks to access a given shared
 * object
 */

public interface JvnObject extends Serializable {
	/*
	 * A JvnObject should be serializable in order to be able to transfer
	 * a reference to a JVN object remotely
	 */

	/**
	 * Get a Read lock on the shared object
	 * 
	 * @throws JvnException
	 **/
	public void jvnLockRead() throws JvnException;

	/**
	 * Get a Write lock on the object
	 * 
	 * @throws JvnException
	 **/
	public void jvnLockWrite() throws JvnException;

	/**
	 * Unlock the object
	 * 
	 * @throws JvnException
	 **/
	public void jvnUnLock() throws JvnException;

	/**
	 * Get the object identification
	 * 
	 * @throws JvnException
	 **/
	public int jvnGetObjectId() throws JvnException;

	/**
	 * Get the shared object associated to this JvnObject
	 * 
	 * @throws JvnException
	 **/
	public Serializable jvnGetSharedObject() throws JvnException;

	/**
	 * Invalidate the Read lock of the JVN object
	 * 
	 * @throws JvnException
	 **/
	public void jvnInvalidateReader() throws JvnException;

	/**
	 * Invalidate the Write lock of the JVN object
	 * 
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriter() throws JvnException;

	/**
	 * Reduce the Write lock of the JVN object
	 * 
	 * @return the current JVN object state
	 * @throws JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader() throws JvnException;

	/**
	 * Change the LocalServer of a JvnObject.
	 * 
	 * @param jvnServerImpl the new server.
	 * @throws JvnException
	 */	
	public void jvnSetServer(JvnLocalServer jvnServerImpl) throws JvnException;

	/* Personal add */
	
	/**
	 * Setter of the Serializable object.
	 * @param serializable The updated shared object.
	 */
	public void jvnSetSharedObject(Serializable serializable);

	/**
	 * Reset the State of a JvnObject to its default value: NL.
	 * @throws JvnException
	 */
	public void resetState() throws JvnException;
	
}
