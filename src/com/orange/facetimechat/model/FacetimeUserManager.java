package com.orange.facetimechat.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;




public class FacetimeUserManager {

	CopyOnWriteArrayList<FacetimeUser> userList = new CopyOnWriteArrayList<FacetimeUser>();
	
	// thread-safe singleton implementation
	private static FacetimeUserManager manager = new FacetimeUserManager();
	private FacetimeUserManager() {
		
	}
	public static FacetimeUserManager getInstance() {
		return manager;
	}
	

	synchronized public boolean addUser(FacetimeUser user){
		return  userList.add(user);
	}
	
	synchronized public void removeUser(FacetimeUser user){
			userList.remove(user);
	}
	
	
	synchronized public FacetimeUser findMatch(FacetimeUser user) {
		if (user.getStatus() == FacetimeUser.MATCHED)
			return user.getMatchedUser();
		else {
			for (FacetimeUser matchedUser : userList) {
				if (matchedUser.isMatched() == false &&
					matchedUser.isMyself(user.getUser().getUserId()) == false ) 
				{
					user.setStatus(FacetimeUser.MATCHED);
					matchedUser.setStatus(FacetimeUser.MATCHED);
					user.setMatchedUser(matchedUser);
					matchedUser.setMatchedUser(user);
					return matchedUser;
				}
			}
			return null;
		}
	}
	
	synchronized public FacetimeUser findUserById(String userId) {
         for (FacetimeUser user: userList) {
        	 if ( user.isMyself(userId))
        		 return user;
         }
		return null;
	}
	
	synchronized public FacetimeUser findUserByChannel(Channel channel) {
		for (FacetimeUser user: userList) {
			if (user.getChannel() == channel)
				return user;
		}
		return null;
	}
	
	// For logger use, to help debug :)
	public CopyOnWriteArrayList<FacetimeUser> getUserList() {
		return userList;
	}
}
