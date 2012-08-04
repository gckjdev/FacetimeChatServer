package com.orange.facetimechat.model;

import java.util.HashMap;
import java.util.Map;
import org.jboss.netty.channel.Channel;


public class FacetimeUserManager {
	Map<String, FacetimeUser> userIdMap = new HashMap<String, FacetimeUser>();
	Map<Channel, FacetimeUser> userChannelMap = new HashMap<Channel, FacetimeUser>();
	
	// thread-safe singleton implementation
	private static FacetimeUserManager manager = new FacetimeUserManager();
	private FacetimeUserManager() {
		
	}
	
	public static FacetimeUserManager getInstance() {
		return manager;
	}
	
	synchronized public void addUser(FacetimeUser user) {
			userIdMap.put(user.getUser().getUserId(),user); 
			userChannelMap.put(user.getChannel(), user);	
		}
	
	synchronized public void removeUser(FacetimeUser user){
			if (user == null || user.getUser() == null)
				return;
		
			userIdMap.remove(user.getUser().getUserId());
			userChannelMap.remove(user.getChannel());
	}
	
	synchronized public FacetimeUser findMatch(FacetimeUser user) {
		if (user.getStatus() == FacetimeUser.MATCHED) {
			return user.getMatchedUser();
		}
		else {				
			for (Map.Entry<String, FacetimeUser> entry : userIdMap.entrySet()){
				FacetimeUser matchedUser = entry.getValue();
				if (matchedUser.isMatched() == false &&
					matchedUser.isMyself(user.getUser().getUserId()) == false &&
					isGenderMatched(user, matchedUser)== true)
				{
					user.setStatus(FacetimeUser.MATCHED);
					matchedUser.setStatus(FacetimeUser.MATCHED);
					user.setMatchedUser(matchedUser);
					matchedUser.setMatchedUser(user);
					
					userIdMap.put(matchedUser.getUser().getUserId(), matchedUser);
					
					return matchedUser;
				}
			}
			return null;
		}
	}
	
	private boolean isGenderMatched(FacetimeUser user,FacetimeUser matchedUser) {
		// Both are picky at gender,so they must match each other's request
		if (user.isFindByGender() == true && matchedUser.isFindByGender() == true)
			return (matchedUser.getUser().getGender() == user.getChatGender() && 
			matchedUser.getChatGender() == user.getUser().getGender());
		// A is picky, B is not, so the B will only be matched if his/her gender is what a wants.
		else if (user.isFindByGender() == true && matchedUser.isFindByGender() == false)
			return user.getChatGender() == matchedUser.getUser().getGender();
		// Same as above condition.
		else if (user.isFindByGender() == false && matchedUser.isFindByGender() == true)
			return matchedUser.getChatGender() == user.getUser().getGender();
		// Neither is picky at gender, just let them match.
		else 
			return true;
	}
	
	synchronized public FacetimeUser findUserById(String userId) {
		FacetimeUser user = userIdMap.get(userId);
		return user;
	}
	
	synchronized public FacetimeUser findUserByChannel(Channel channel) {
		FacetimeUser user = userChannelMap.get(channel);
		return user;
	}
	
	public String getUserInMap() {
		// There is a circle,so we explictly use StringBuilder
		// to improve the String building performance.
		// (see Thinking in Java[edition 4]. Ch13)
		StringBuilder result = new StringBuilder("\n");
		for (Map.Entry<String, FacetimeUser> entry : userIdMap.entrySet()) {
			FacetimeUser user = entry.getValue();
			result.append(user);
			result.append("  \tgender:");
			result.append(user.getUserGender());
			result.append("  \tchatgender:");
			result.append((user.isFindByGender()? user.getChatGender() : "None"));
			result.append("\n");
		}
		
		return result.toString();
	
	}

}