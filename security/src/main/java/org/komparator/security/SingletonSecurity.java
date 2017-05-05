package org.komparator.security;

public class SingletonSecurity {
	
	private String wsName;
	
	private static class SingletonHolder {
		private static final SingletonSecurity INSTANCE = new SingletonSecurity();
	}

	public static synchronized SingletonSecurity getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public String getWsName() {
		return wsName;
	}

	public void setWsName(String wsName) {
		this.wsName = wsName;
	}
}