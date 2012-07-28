package com.orange.facetimechat.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;


public class StatisticService {

	private static final Logger logger = Logger.getLogger(StatisticService.class.getName());
	
	// thread-safe singleton implementation
	private static StatisticService defaultService = new StatisticService();
	// Supress default constructor for noninstantiablity
	private StatisticService(){
		
	}
	public static StatisticService getInstance() {
		return defaultService;
	}
	
	CopyOnWriteArrayList<String> facetimeUserList = new CopyOnWriteArrayList<String>();
	ConcurrentHashMap<String, String> facetimeMatchUserList = new ConcurrentHashMap<String, String>();
	
	public void addNewFacetime(String userId){
		facetimeUserList.add(userId);
		logger.info("<STATISTIC> total add user count="+facetimeUserList.size());
	}
	
	synchronized public void addFacetimeMatch(String userId, String matchedUserId){
		try {
			if ( isMatchPairAdded(userId, matchedUserId) == true ) {
				return;
			}
			facetimeMatchUserList.put(userId, matchedUserId);
			logger.info("<STATISTIC> match user map count="+ facetimeMatchUserList.size() 
					+ ", map=" + facetimeMatchUserList.toString());
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}
	
	private boolean isMatchPairAdded(String userId, String matchedUserId) throws Exception {
		if (facetimeMatchUserList.containsKey(userId) ) {
			
			if ( !facetimeMatchUserList.get(userId).equals(matchedUserId)) {
				// "Pre" added to differentiate with Post.
				logger.error("<STATISTIC> Pre!!! " + userId + " want to match another one :"+
						facetimeMatchUserList.get(userId) + "!!!");
				throw new Exception("duplicate match!!!:"+ userId);
			} else 
				return true;
		
		} 
		else if (facetimeMatchUserList.containsValue(userId))
		{
			if ( !facetimeMatchUserList.get(matchedUserId).equals(userId)) {
				// "Post" added to differentiate with Pre.
				logger.error("<STATISTIC> Post!!! " + matchedUserId + " want to match another one:"+ 
						facetimeMatchUserList.get(matchedUserId)+ "!!!");
				throw new Exception("duplicate match!!!:"+ matchedUserId);
			} else 
				return true;
		
		}
		return false;
	}
	
}
