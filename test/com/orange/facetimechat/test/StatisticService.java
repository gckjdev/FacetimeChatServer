package com.orange.facetimechat.test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.log4j.Logger;
import org.apache.thrift.transport.TFileTransport.chunkState;

import com.orange.facetimechat.model.FacetimeUser;
import com.orange.facetimechat.model.FacetimeUserManager;
import com.orange.game.model.manager.UserManager;

public class StatisticService {

	private static final Logger logger = Logger.getLogger(StatisticService.class.getName());
//	private AtomicInteger doubleMatchCount = new AtomicInteger(0);
//	private FacetimeUserManager userManager = FacetimeUserManager.getInstance();
	
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
//	ConcurrentHashMap<String, String> facetimeMatchUserList2 = new ConcurrentHashMap<String, String>();
	
	public void addNewFacetime(String userId){
		facetimeUserList.add(userId);
		logger.info("<STATISTIC> total add user count="+facetimeUserList.size());
	}
	
	public void addFacetimeMatch(String userId, String matchedUserId){
		try {
			if ( isMatchPairAdded(userId, matchedUserId) == true ) {
				return;
			}
			facetimeMatchUserList1.put(userId, matchedUserId);
			logger.info("<STATISTIC> match user map count="+ facetimeMatchUserList1.size() 
					+ ", map="+facetimeMatchUserList1.toString());
		} catch (Exception e) {
			logger.error("<STATISTIC> duplicated match error!!!");
		}
	}
	
	private boolean isMatchPairAdded(String userId, String matchedUserId) throws Exception {
		if (facetimeMatchUserList1.containsKey(userId) ) {
			
			if ( !facetimeMatchUserList1.get(userId).equals(matchedUserId)) {
				logger.error("<STATISTIC> A !!! " + userId + " matched another one :"+
						facetimeMatchUserList1.get(userId) + "!!!");
				throw new Exception("duplicate match!!!:"+ userId);
			} else 
				return true;
		
		} 
		else if (facetimeMatchUserList1.containsValue(userId))
		{
			
			if ( !facetimeMatchUserList1.get(matchedUserId).equals(userId)) {
				logger.error("<STATISTIC> P !!! " + matchedUserId + " matched another one:"+ 
						facetimeMatchUserList1.get(matchedUserId)+ "!!!");
				throw new Exception("duplicate match!!!:"+ matchedUserId);
			} else 
				return true;
		
		}
		return false;
	}
}
