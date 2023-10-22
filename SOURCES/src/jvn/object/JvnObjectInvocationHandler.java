package jvn.object;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JvnObjectInvocationHandler implements InvocationHandler{
	JvnObject object;
	
	public JvnObjectInvocationHandler(JvnObject obj) {
		this.object = obj;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable, IllegalArgumentException {
		// We have a read method, and a write method, that redirects to the right function
		// I guess we do lock read and lock write here
		Object result;
		switch (method.getName()) {
			case "read":
				object.jvnLockRead();
				break;
			case "write":
				object.jvnLockWrite();
				break;
			default:
				throw new IllegalArgumentException("Error: only read and write exist");
		}
		result = method.invoke(object.jvnGetSharedObject(), args);
		object.jvnUnLock();
		return result;
	}

}
