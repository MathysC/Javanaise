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
    
    public JvnObjectImpl(int id, Serializable shared) {
        this.id = id;
        this.sharedObject = shared;
        this.server = server;
    }
    @Override
    public void jvnLockRead() throws JvnException {
    	this.server.jvnLockRead(id);
    	this.state = LockState.R;
    }

    @Override
    public void jvnLockWrite() throws JvnException {
    	this.server.jvnLockWrite(id);
    	this.state = LockState.W;
    }

    @Override
    public void jvnUnLock() throws JvnException {
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
    }

    @Override
    public Serializable jvnInvalidateWriter() throws JvnException {
        return null;
    }

    @Override
    public Serializable jvnInvalidateWriterForReader() throws JvnException {
        return null;
    }

}
