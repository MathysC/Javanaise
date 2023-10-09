package jvn.object;

import java.io.Serializable;
import java.rmi.Remote;

import jvn.server.JvnLocalServer;
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
    public void jvnLockRead() throws JvnException {
    	switch (this.state) {
    		case W:
    		case WC:
    		case RC:
    			this.state = LockState.R;
    			break;
    		default:
    			Serializable sr = this.server.jvnLockRead(id);
        		this.jvnSetSharedObject(sr);
            	this.state = LockState.R;
            	break;
    	}
    	
    }

    @Override
    public void jvnLockWrite() throws JvnException {
    	switch (this.state) {
    		case W:
    		case WC:
    			break;
    		case R:
    		case RC:
    			this.state = LockState.W;
    			break;
    		default:
    			this.jvnSetSharedObject(this.server.jvnLockWrite(id));
            	this.state = LockState.W;
    	}
    }

    @Override
    public void jvnUnLock() throws JvnException {
    	switch (this.state) {
    	case R:
    		this.state = LockState.RC;
    		break;
    	case W:
    		this.state = LockState.WC;
    		break;
    	}
    }

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
    
    public void jvnSetServer(JvnLocalServer server) {
    	this.server = server;
    }

    @Override
    public void jvnInvalidateReader() throws JvnException {
    	switch (this.state) {
	    	case RC:		
	    		break;
	    	case R:
	    		// wait for RC
	    		break;
			default:
				// Anything else shouldn't be possible
				throw new JvnException("Error : Impossible state in ObjectImpl");
    	}
    	this.state = LockState.NL;
    	// RC -> NL
    	// R -> wait (RC)
    	// W -> wait (WC)
    	// WC -> NL 
    	// WRC -> wait (RC)
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
    	switch (this.state) {
    	case WC:
    		break;
    	case W:
    		// wait for WC
    		break;
		default:
			// Anything else shouldn't be possible
			throw new JvnException("Error : Impossible state in ObjectImpl");
    	}
    	this.state = LockState.NL;
    	return this.jvnGetSharedObject(); // return the object to be able to update the value
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }

}
