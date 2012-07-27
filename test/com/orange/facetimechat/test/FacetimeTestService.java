package com.orange.facetimechat.test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

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
	private static final AtomicInteger userIndex = new AtomicInteger(1);
	
	Channel channel;
	String userId;
	
	public FacetimeTestService(){
	}

	public synchronized void setChannel(Channel channel){
		this.channel = channel;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public void simulateMatchRequest(){
		GameMessage message = makeMatchRequest();
		if (channel != null && channel.isWritable()){
			channel.write(message);
			logger.info("<simulateMatchRequest> send message="+message.toString());
			
			StatisticService.getInstance().addNewFacetime(message.getFacetimeChatRequest().getUser().getUserId());
		}
		else{
			logger.info("<simulateMatchRequest> channel is null or not writable\n");
		}
	}

	private GameMessage makeMatchRequest() {
		
		// send a user match request here
		Random random = new Random();
		boolean gender = random.nextBoolean();
		String nickName =  gender == true? "Male_"+userIndex.toString():"Female_" +userIndex.toString();
		userId = "test_user_"+ userIndex.getAndIncrement();//random.nextInt(1000000);
		PBGameUser user = PBGameUser.newBuilder()
			.setUserId(userId)  
			.setGender(gender)
			.setAvatar("http://pic1a.nipic.com/2008-09-02/20089210324895_2.jpg")
			.setNickName(nickName)
			.build();			
		
		FacetimeChatRequest chatRequest = FacetimeChatRequest.newBuilder()
			.setUser(user)
//			.setChatGender(random.nextBoolean())
			.build();
		
		GameMessage message = GameMessage.newBuilder()
			.setCommand(GameCommandType.FACETIME_CHAT_REQUEST)
			.setMessageId(1)
			.setFacetimeChatRequest(chatRequest)
			.build();
		
		return message;
		
	}
	

	public void simulateFacetimeStartRequest(GameMessage message){
		// send a start request
		logger.info("<simulateFacetimeStartRequeset> start Facetime chatting !\n" +
				"###[" + message.getUserId() + 
				"  <-->  " + message.getFacetimeChatResponse().getUser(0).getUserId() + "]###");
	}

}
