package com.orange.facetimechat.model;

import java.util.concurrent.CopyOnWriteArrayList;



public class FacetimeUserManager {

	CopyOnWriteArrayList<FacetimeUser> userList = new CopyOnWriteArrayList<FacetimeUser>();
	
	// thread-safe singleton implementation
	private static FacetimeUserManager manager = new FacetimeUserManager();
	private FacetimeUserManager() {
		
	}
	public static FacetimeUserManager getInstance() {
		return manager;
	}
	

	public boolean createUser(FacetimeUser user){
		// TODO
		return true;
	}
	
	public void removeUser(FacetimeUser user){
		// TODO
	}
	
	
}
