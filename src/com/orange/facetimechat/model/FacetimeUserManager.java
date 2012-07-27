package com.orange.facetimechat.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;


public class FacetimeUserManager {
	private static final Logger logger = Logger.getLogger(FacetimeUserManager.class.getName());
	
//	CopyOnWriteArrayList<FacetimeUser> userList = new CopyOnWriteArrayList<FacetimeUser>();
	
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
	
	synchronized public FacetimeUser findMatch(FacetimeUser user, boolean findByGender) {
		if (user.getStatus() == FacetimeUser.MATCHED) {
			return user.getMatchedUser();
		}
		else {				
			for (Map.Entry<String, FacetimeUser> entry : userIdMap.entrySet()){
				FacetimeUser matchedUser = entry.getValue();
				if (matchedUser.isMatched() == false &&
					matchedUser.isMyself(user.getUser().getUserId()) == false &&
					isGenderMatch(findByGender, user, matchedUser))
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
	
	private boolean isGenderMatch(Boolean findByGender,FacetimeUser user,FacetimeUser matchedUser) {
		if( findByGender == false ) 
			return true;
		else 
			return  matchedUser.getUser().getGender() == user.getChatGender() && 
			matchedUser.getChatGender() == user.getUser().getGender();
	}
	
	synchronized public FacetimeUser findUserById(String userId) {
		FacetimeUser user = userIdMap.get(userId);
		return user;
	}
	
	synchronized public FacetimeUser findUserByChannel(Channel channel) {
		FacetimeUser user = userChannelMap.get(channel);
		return user;
	}
	
	synchronized public String getUserInMap() {
		String resultString="";
		
		Iterator<Map.Entry<String, FacetimeUser>> iterator = userIdMap.entrySet().iterator();
		while (iterator.hasNext()) {
			resultString = 
				resultString + '\n' +((Map.Entry<String, FacetimeUser>)iterator.next() ).getValue().toString();
		}
		resultString += '\n';
		
		return resultString;
	}

	synchronized public void printMap(String func) {
		
		Iterator<Map.Entry<String, FacetimeUser>> iterator = userIdMap.entrySet().iterator();
		logger.info("\n<" + func + ">The map's key/value are listed below:\n");
		while (iterator.hasNext()) {
			logger.info("key:" + ((Map.Entry<String, FacetimeUser>)iterator.next()).getKey()
					+ " value:" + ((Map.Entry<String, FacetimeUser>)iterator.next()).getValue() + "\n");
		}
		
	}
	
// * For userList version
//	
//synchronized public void addUser(FacetimeUser user){
//	userList.add(user);
//}
//	
//synchronized public void removeUser(FacetimeUser user) {
//	userList.remove(user);
//}
//
//synchronized public FacetimeUser findMatch(FacetimeUser user) {
//	if (user.getStatus() == FacetimeUser.MATCHED)
//		return user.getMatchedUser();
//	else {
//		for (FacetimeUser matchedUser : userList) {
//	
//			if (matchedUser.isMatched() == false &&
//				matchedUser.isMyself(user.getUser().getUserId()) == false ) 
//			{
//					user.setStatus(FacetimeUser.MATCHED);
//					matchedUser.setStatus(FacetimeUser.MATCHED);
//					user.setMatchedUser(matchedUser);
//					matchedUser.setMatchedUser(user);
//	
//					return matchedUser;
//				}
//			}
//			return null;
//		}
//	}
//
//synchronized public FacetimeUser findUserById(String userId) {
//  for (FacetimeUser user: userList) {
//    	 if ( user.isMyself(userId))
//    		 return user;
//     }
//	return null;
//}
//	
//synchronized public FacetimeUser  findUserByChannel(Channel channel) {
//	for (FacetimeUser user: userList) {
//	if (user.getChannel() == channel)
//		return user;
//}
//return null;
//}
//
//	
////For logger use, to help debug :)
//public String getUserList() {
//	return userList.toString();
//}
  
}
