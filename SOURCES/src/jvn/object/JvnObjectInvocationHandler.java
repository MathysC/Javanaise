package jvn.object;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jvn.annotations.Operation;

public class JvnObjectInvocationHandler implements InvocationHandler, Serializable{
	private static final long serialVersionUID = 1L;
	JvnObject object;
	
	public JvnObjectInvocationHandler(JvnObject obj) {
		this.object = obj;
	}
	
	public static Object newInstance(JvnObject obj) {
		JvnObject ret = (JvnObject) Proxy.newProxyInstance(
				JvnObject.class.getClassLoader(), 
				new Class[] {JvnObject.class}, 
				new JvnObjectInvocationHandler(obj)
		);
		return ret;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable, IllegalArgumentException {
		Object result;
		// If the annotation is present, we are in a read / write situation
		if (method.isAnnotationPresent(Operation.class)) {
			switch (method.getAnnotation(Operation.class).name()) {
				case "read":
					object.jvnLockRead();
					break;
				case "write":
					object.jvnLockWrite();
					break;
				default:
					throw new IllegalArgumentException("Error: Only 'read' and 'write' are valid Operation names");
			}
			// This is necessary because else the method points to a JvnObject instead of a sentence.
			// comparing with classmates, it seems this is due to an issue on our side somewhere else, but it currently works
			// and we will solve it if we have the time.
			Method objMethod = object.jvnGetSharedObject().getClass().getMethod(method.getName(), method.getParameterTypes());
			result = objMethod.invoke(object.jvnGetSharedObject(), args);
			object.jvnUnLock();
		} else {
			result = method.invoke(object, args);
		}
		return result;
	}

}
