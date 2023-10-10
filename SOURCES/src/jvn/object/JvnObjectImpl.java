package jvn.object;

import java.io.Serializable;
import java.rmi.Remote;

import jvn.server.JvnLocalServer;
import jvn.server.JvnRemoteServer;
import jvn.server.JvnServerImpl;
import jvn.utils.JvnException;
import jvn.utils.LockState;

public class JvnObjectImpl implements Remote, JvnObject {

    private int id;
    private Serializable sharedObject;
    // needs to be transient else it breaks everything. 
    // Because server is not really a server, it's some sort of stub
    // so when this is serialized, it converts the stub to something else
    // and tries to convert back the something else to a jvnlocaleserver
    // but it never was that, it was a strange stub, so it bugs.
    private transient JvnLocalServer server;
    private LockState state;

    public JvnObjectImpl(Serializable shared,int id, JvnLocalServer server) {
        this.id = id;
        this.sharedObject = shared;
        this.server = server;
        this.state = LockState.NL;
    }
    
    @Override
    public synchronized void jvnLockRead() throws JvnException {
    	switch (this.state) {
    		case RC:
    			this.rcReached = false;
    			this.state = LockState.R;
    			break;
    		case R:	
    			break;
    		case WC:
        		this.jvnSetSharedObject(this.server.jvnLockRead(id));
        		this.state = LockState.RWC;
    		case RWC:
            	break;
    		default:
        		this.jvnSetSharedObject(this.server.jvnLockRead(id));
            	this.state = LockState.R;
            	break;
    	}
    	System.out.println("Locked in read");
    	
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
    	switch (this.state) {
    		case WC:
    			this.wcReached = false;
    		case W:
    			this.state = LockState.W;
    			break;
    		default:
    			this.jvnSetSharedObject(this.server.jvnLockWrite(id));
            	this.state = LockState.W;
    	}
    	System.out.println("Locked in write");	
    }

    private boolean rcReached = false;
    private boolean wcReached = false;
    
    @Override
    public synchronized void jvnUnLock() throws JvnException {
    	switch (this.state) {
    	case R:
    		this.state = LockState.RC;
    		this.rcReached = true;
    		break;
    	case W:
    		this.state = LockState.WC;
    		this.wcReached = true;
    		break;
    	}
    	notify();
    	System.out.println("Unlocked");
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
    	switch (this.state) {
	    	case RC:
	    		break;
	    	case R:
	    		while (!this.rcReached) {
	    			try {
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
	    		this.rcReached = false;
	    		break;
			default:
				// Anything else shouldn't be possible
				throw new JvnException("Error : Impossible state in ObjectImpl");
    	}
    	this.state = LockState.NL;
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
    	switch (this.state) {
    	case WC:
    		break;
    	case W:
    		while (!this.wcReached) {
    			try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		this.wcReached = false;
    		break;
		default:
			// Anything else shouldn't be possible
			throw new JvnException("Error : Impossible state in ObjectImpl");
    	}
    	this.state = LockState.NL;
    	return this.jvnGetSharedObject(); // return the object to be able to update the value
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }
    
    /* -- Getters / Setters -- */
    @Override
    public int jvnGetObjectId() throws JvnException {
        return this.id;
    }

    @Override
    public Serializable jvnGetSharedObject() throws JvnException {
        return this.sharedObject;
    }

    public void jvnSetSharedObject(Serializable obj) throws JvnException {
        this.sharedObject = obj;
    }
    
    @Override
    public void jvnSetServer(JvnLocalServer server) throws JvnException{
    	this.server = server;
    }


}
