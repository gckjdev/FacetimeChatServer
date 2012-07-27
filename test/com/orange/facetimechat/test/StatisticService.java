package com.orange.facetimechat.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.orange.facetimechat.model.FacetimeUser;
import com.orange.facetimechat.model.FacetimeUserManager;
import com.orange.game.model.manager.UserManager;

public class StatisticService {

	private static final Logger logger = Logger.getLogger(StatisticService.class.getName());
	private AtomicInteger doubleMatchCount = new AtomicInteger(0);
	private FacetimeUserManager userManager = FacetimeUserManager.getInstance();
	
	// thread-safe singleton implementation
	private static StatisticService defaultService = new StatisticService();
	// Supress default constructor for noninstantiablity
	private StatisticService(){
		
	}
	public static StatisticService getInstance() {
		return defaultService;
	}
	
	CopyOnWriteArrayList<String> facetimeUserList = new CopyOnWriteArrayList<String>();
	ConcurrentHashMap<String, String> facetimeMatchUserList1 = new ConcurrentHashMap<String, String>();
	ConcurrentHashMap<String, String> facetimeMatchUserList2 = new ConcurrentHashMap<String, String>();
	
	public void addNewFacetime(String userId){
		facetimeUserList.add(userId);
		logger.info("[STATISTIC] total add user count="+facetimeUserList.size());
	}
	
	public void addFacetimeMatch(String userId, String matchedUserId){
		if (facetimeMatchUserList1.containsKey(userId)){
			// already has user 
			int count = doubleMatchCount.incrementAndGet();
			logger.info("double match count="+count);
			
			logger.warn("[STATISTIC] user id has been added... userId="+userId+
					", matchUserId="+facetimeMatchUserList1.get(userId)+", try to add "+matchedUserId);
			return;
		}
		facetimeMatchUserList1.put(userId, matchedUserId);
		logger.info("[STATISTIC] match user map count="+ facetimeMatchUserList1.size() + ", map="+facetimeMatchUserList1.toString());
//		facetimeMatchUserList2.put(matchedUserId, userId);		
	}
}
