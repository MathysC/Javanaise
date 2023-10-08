package jvn.object;

import java.io.Serializable;

import jvn.server.JvnServerImpl;
import jvn.utils.JvnException;

public class JvnObjectImpl implements JvnObject {

    private int id;
    private Serializable sharedObject;

    public JvnObjectImpl(int id) {
        this.id = id;
    }
    @Override
    public void jvnLockRead() throws JvnException {
    }

    @Override
    public void jvnLockWrite() throws JvnException {
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
