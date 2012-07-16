package com.orange.facetimechat.test;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.orange.network.game.protocol.constants.GameConstantsProtos.GameCommandType;
import com.orange.network.game.protocol.message.GameMessageProtos.FacetimeChatRequest;
import com.orange.network.game.protocol.message.GameMessageProtos.GameMessage;
import com.orange.network.game.protocol.model.GameBasicProtos.PBGameUser;



public class FacetimeTestService {

	private static final Logger logger = Logger.getLogger(FacetimeTestService.class.getName());	
	Channel channel;
	
	public FacetimeTestService(){
	}

	public synchronized void setChannel(Channel channel){
		this.channel = channel;
	}
	
	public void simulateMatchRequest(){
		// send a user match request here
		PBGameUser user = PBGameUser.newBuilder()
			.setUserId("test_user_1")
			.setGender(true)
			.setNickName("Jian Yu")
			.build();			
		
		FacetimeChatRequest chatRequest = FacetimeChatRequest.newBuilder()
			.setUser(user)
			.build();
		
		GameMessage message = GameMessage.newBuilder()
			.setCommand(GameCommandType.FACETIME_CHAT_REQUEST)
			.setMessageId(1)
			.setFacetimeChatRequest(chatRequest)
			.build();
		
		if (channel != null && channel.isWritable()){
			channel.write(message);
			logger.info("<simulateMatchRequest> send message="+message.toString());
		}
		else{
			logger.info("<simulateMatchRequest> channel is null or not writable");
		}
	}

	public void simulateFacetimeStartRequest(){
		// send a start request
	}


}
