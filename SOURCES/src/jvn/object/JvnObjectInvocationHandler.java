package jvn.object;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
		// We have a read method, and a write method, that redirects to the right function
		// I guess we do lock read and lock write here
		Object result;
		switch (method.getName()) {
			case "read":
				object.jvnLockRead();
				// method points to a method linked to the JvnObject if i don't do that
				Method readMethod = object.jvnGetSharedObject().getClass().getMethod(method.getName(), method.getParameterTypes());
				result = readMethod.invoke(object.jvnGetSharedObject(), args);
				object.jvnUnLock();
				break;
			case "write":
				object.jvnLockWrite();
				Method writeMethod = object.jvnGetSharedObject().getClass().getMethod(method.getName(), method.getParameterTypes());
				result = writeMethod.invoke(object.jvnGetSharedObject(), args);
				object.jvnUnLock();
				break;
			default:
				result = method.invoke(object, args);
				break;
		}
		
		return result;
	}

}
