package com.orange.facetimechat.test;

import java.util.Map;
import java.util.Random;

import org.antlr.grammar.v3.ANTLRv3Parser.id_return;
import org.apache.cassandra.cli.CliParser.countStatement_return;
import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.cassandra.thrift.Cassandra.set_keyspace_args;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.orange.facetimechat.model.FacetimeUser;
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
		Random random = new Random();
		PBGameUser user = PBGameUser.newBuilder()
			.setUserId("test_user_"+ random.nextInt(10000))
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
			logger.info("<simulateMatchRequest> channel is null or not writable"
					+ "Channel isBound: " + channel.isBound() + '\n'
					+ "channel isConnected: " + channel.isConnected() +'\n'
					+ "channel is isOpen: " + channel.isOpen());
		}
	}

	

	public void simulateFacetimeStartRequest(GameMessage message){
		// send a start request
		logger.info("<simulateFacetimeStartRequeset> start Facetime chatting !\n" +
				"###[" + message.getUserId() + 
				"  <-->  " + message.getFacetimeChatResponse().getUser(0).getUserId() + "]###");
	}


}
