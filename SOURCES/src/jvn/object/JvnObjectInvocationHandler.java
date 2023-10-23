package jvn.object;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jvn.annotations.Operation;
import jvn.utils.JvnException;

public class JvnObjectInvocationHandler implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	JvnObject object;

	public JvnObjectInvocationHandler(JvnObject obj) {
		this.object = obj;
	}

	public static Object newInstance(JvnObject obj) throws JvnException {
		Object shared = obj.jvnGetSharedObject();
		return Proxy.newProxyInstance(
				shared.getClass().getClassLoader(),
				shared.getClass().getInterfaces(),
				new JvnObjectInvocationHandler(obj));
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
			result = method.invoke(object.jvnGetSharedObject(), args);
			object.jvnUnLock();
		} else {
			result = method.invoke(object.jvnGetSharedObject(), args);
		}

		return result;
	}

}
