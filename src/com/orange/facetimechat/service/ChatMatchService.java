package com.orange.facetimechat.service;

import com.orange.facetimechat.model.FacetimeUserManager;
import com.orange.network.game.protocol.message.GameMessageProtos.GameMessage;

public class ChatMatchService {

	final FacetimeUserManager userManager = FacetimeUserManager.getInstance();
	
	// thread-safe singleton implementation
	private static ChatMatchService defaultService = new ChatMatchService();
	private ChatMatchService() {
		
	}
	public static ChatMatchService getInstance() {
		return defaultService;
	}
	
	public void matchUserChatRequest(GameMessage message){
		
		// TODO 
	}
	
	public void userStartFacetime(GameMessage message) {
		// TODO Auto-generated method stub		
	}
}
