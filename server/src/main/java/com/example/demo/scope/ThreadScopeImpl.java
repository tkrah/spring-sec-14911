package com.example.demo.scope;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

public class ThreadScopeImpl implements Scope {

	CustomThreadLocal customThreadLocal = new CustomThreadLocal();

	@Override
	public Object get(String str, ObjectFactory objectFactory) {
		@SuppressWarnings("unchecked")
		Map<String, Object> scope = (Map<String, Object>) customThreadLocal.get();
		Object object = scope.get(str);
		if (object == null) {
			object = objectFactory.getObject();
			scope.put(str, object);
		}
		return object;
	}

	public void clearScope() {
		customThreadLocal.remove();
	}

	@Override
	public String getConversationId() {
		return null;
	}

	@Override
	public void registerDestructionCallback(String arg0, Runnable arg1) {
	}

	@Override
	public Object remove(String str) {
		Map<String, Object> scope = (Map<String, Object>) customThreadLocal.get();
		return scope.remove(str);
	}

	@Override
	public Object resolveContextualObject(String arg0) {
		return null;
	}

	class CustomThreadLocal extends ThreadLocal {
		protected Map<String, Object> initialValue() {
			return new HashMap<>();
		}
	}

}
