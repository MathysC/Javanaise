/***
 * JAVANAISE Implementation
 * JvnObject class
 * This class implements the Javanaise object
 * Contact:
 *
 * Authors: MathysC MatveiP
 */
package jvn.object;

import java.io.Serializable;
import java.rmi.Remote;

import jvn.server.JvnLocalServer;
import jvn.utils.JvnException;
import jvn.utils.State;

public class JvnObjectImpl implements Remote, JvnObject {

    /**
     * Serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identification number of the Object.
     */
    private int id;

    /**
     * The shared object contained by the JvnObject.
     */
    private Serializable sharedObject;

    /**
     * The server linked to the Object.
     * 
     * @implNote Needs to be transient else it breaks everything.
     *           Because server is not really a server, it's some sort of stub
     *           so when this is serialized, it converts the stub to something else
     *           and tries to convert back the something else to a jvnlocaleserver
     *           but it never was that, it was a strange stub, so it bugs.
     */
    private transient JvnLocalServer server;

    /**
     * The state of the object.
     */
    private State state;

    public JvnObjectImpl(Serializable shared, int id, JvnLocalServer server) {
        this.id = id;
        this.sharedObject = shared;
        this.server = server;
        this.state = State.W;
    }

    @Override
    public synchronized void jvnLockRead() throws JvnException {
        switch (this.state) {
            case NL:
                this.state = State.R;
                this.sharedObject = this.server.jvnLockRead(this.id);
                break;
            case RC:
                this.state = State.R;
                break;
            case WC:
                this.state = State.RWC;
                break;
            default:
                this.error("jvnLockRead", "No Lock Read on " + this.state);
                break;
        }
    }

    @Override
    public synchronized void jvnLockWrite() throws JvnException {
        switch (this.state) {
            case WC:
            case RWC:
                this.state = State.W;
                break;
            case NL:
            case RC:
                this.state = State.W;
                this.sharedObject = this.server.jvnLockWrite(this.id);
                break;
            default:
                this.error("jvnLockWrite", "No Lock Write on " + this.state);
                break;
        }
    }

    @Override
    public synchronized void jvnUnLock() throws JvnException {
        switch (this.state) {
            case RWC:
            case W:
                this.state = State.WC;
                break;
            case R:
                this.state = State.RC;
                break;
            default:
                this.error("jvnUnLock", "No Unlock on " + this.state);
                break;
        }
        this.notifyAll();
    }

    @Override
    public synchronized void jvnInvalidateReader() throws JvnException {
        while (this.state != State.RC) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                this.error("jvnInvalidateReader", null);
                ex.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        this.state = State.NL;
    }

    @Override
    public synchronized Serializable jvnInvalidateWriter() throws JvnException {
        while (this.state != State.WC) {
            try {
                this.wait();
            } catch (InterruptedException ex) {
                this.error("jvnInvalidateWriter", null);
                ex.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        this.state = State.NL;
        return this.sharedObject;
    }

    @Override
    public synchronized Serializable jvnInvalidateWriterForReader() throws JvnException {
        switch (state) {
            case W:
                while (this.state == State.W) {
                    try {
                        this.wait();
                    } catch (InterruptedException ex) {
                        this.error("jvnInvalidateWriterForReader", null);
                        ex.printStackTrace();
                        Thread.currentThread().interrupt();

                    }
                }
                state = State.RC;
                break;
            case RWC:
            case WC:
                state = State.RC;
                break;
            default:
                this.error("jvnInvalidateWriterForReader", "No Invalidate Writer on " + this.state);
        }
        return this.sharedObject;
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

    @Override
    public void jvnSetSharedObject(Serializable serializable) {
        this.sharedObject = serializable;
    }

    @Override
    public void jvnSetServer(JvnLocalServer server) throws JvnException {
        this.server = server;
    }

    @Override
    public void resetState() throws JvnException {
        this.state = State.NL;
    }

    /**
     * Write an error message in the Console.
     * 
     * @param func The name of the function.
     * @param m    The message to print.
     */
    private void error(String func, String m) {
        m = m == null ? "An error occurs while waiting" : m;
        System.err.println("[" + JvnObjectImpl.class.getName() + "][" + func + "]: " + m);
    }

    @Override
    public String read() throws JvnException {
        // not necessary to fill the method, it's just there
        // because the proxy needs to know that writing and reading is possible
        throw new IllegalArgumentException("This method call is not intended.");
    }

    @Override
    public void write(String s) throws JvnException {
        // not necessary to fill the method, it's just there
        // because the proxy needs to know that writing and reading is possible
        throw new IllegalArgumentException("This method call is not intended.");
    }

}
