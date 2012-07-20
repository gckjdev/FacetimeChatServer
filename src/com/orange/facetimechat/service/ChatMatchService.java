package com.orange.facetimechat.service;

import org.apache.cassandra.thrift.Cassandra.set_keyspace_args;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelStateEvent;
import org.omg.CORBA.PUBLIC_MEMBER;

import com.orange.facetimechat.model.FacetimeUser;
import com.orange.facetimechat.model.FacetimeUserManager;
import com.orange.facetimechat.test.FacetimeTestService;
import com.orange.network.game.protocol.constants.GameConstantsProtos.GameCommandType;
import com.orange.network.game.protocol.message.GameMessageProtos.FacetimeChatRequest;
import com.orange.network.game.protocol.message.GameMessageProtos.FacetimeChatResponse;
import com.orange.network.game.protocol.message.GameMessageProtos.GameMessage;
import com.orange.network.game.protocol.model.GameBasicProtos.PBGameUser;
import com.sun.corba.se.spi.ior.Writeable;

public class ChatMatchService {

	final FacetimeUserManager userManager = FacetimeUserManager.getInstance();
	private static final Logger logger = Logger
			.getLogger(FacetimeTestService.class.getName());

	// thread-safe singleton implementation
	private static ChatMatchService defaultService = new ChatMatchService();

	private ChatMatchService() {

	}

	public static ChatMatchService getInstance() {
		return defaultService;
	}
	
	
	private void setSentFacetimeResponse(FacetimeUser user) {
		user.setSentFacetimeResponse();
	}
	
	private boolean getSentFacetimeResponse(FacetimeUser user) {
		return user.getSentFacetimeResponse();
	}

	private void sendFacetimeResponse(FacetimeUser user,FacetimeUser matchedUser) {
		// This synchronized block act as a superviser, who
		//  checks whether a response has sent. If yes,
		//   then do noting,just return.
		// * Use the first argument(user) to sychronize,
		// * so when server is deciding whether to sent A a response.
		// * it won't do the sameting to A's matchup B, if B is scheduled. 
		synchronized (user) {
			if (user.getSentFacetimeResponse() == true)
				return;
		}
		
		FacetimeChatResponse chatResponse = FacetimeChatResponse.newBuilder()
				.addUser(matchedUser.getUser())
				.setChosenToInitiate(user.isChosenToInitiate())
				.build();

		GameMessage message = GameMessage.newBuilder()
				.setCommand(GameCommandType.FACETIME_CHAT_RESPONSE)
				.setMessageId(1)
				.setFacetimeChatResponse(chatResponse)
				.setUserId(user.getUser().getUserId())
				.build();

		Channel channel = user.getChannel();
		if (channel != null && channel.isWritable()) {
			// When sent user a response, we should 
			// set it's status, this two actions should
			// be do as a atomic action.
			synchronized (user) {
				channel.write(message);
				setSentFacetimeResponse(user);
			}
			logger.info("<sendFacetimeResponse> send message="
					+ message.toString());
		} else {
			logger
					.info("<sendFacetimeResponse> channel is null or not writable");
		}
	}
	

	public void matchUserChatRequest(GameMessage message, Channel channel) {

		FacetimeUser user = new FacetimeUser(message.getFacetimeChatRequest()
				.getUser(), channel);
		FacetimeUser matchedUser = null;
		FacetimeUserManager userManager = FacetimeUserManager.getInstance();

		// Add the user into userManager's userList and pick a user to match.
		synchronized (this) {
			userManager.addUser(user);
			logger.info(user + " is added to uerList,\n" + "now the userList is " +
				userManager.getUserList().toString());
		}
		
		matchedUser = userManager.findMatch(user);
		if (matchedUser == null) {
			logger
					.info("<matchUserChatRequest> " + user +" not found a user, waiting...\n"
					+ "the userList now is " + userManager.getUserList().toString());
			return;
		}
		logger
				.info("<matchUserChatRequest> " + user + " found a match user： "
						+ matchedUser + "\nthe userList now is: " 
						+ userManager.getUserList().toString());

		// Choose who to initiate the chatting.By default we choose the user.
		chooseOnetoInitiate(user, matchedUser);

		// Here we have got a matched pair and decided who to initiate!
		// Send a response respectively. We shall avoid
		// sending one duplicate responses. Imagine this:
		// In A's thread, server sends A a response, 
		// then in A's matchup B's thread, server sends A a response again!
		//  So sendFacetimeResponse should be coded to  tackle this.
			sendFacetimeResponse(user, matchedUser);
			sendFacetimeResponse(matchedUser, user);
	}


	private void chooseOnetoInitiate(FacetimeUser user, FacetimeUser matchedUser) {
		// This is a default behaviour: the user is chosen !
		// It is more fair to ramdomly choose one, but this may compromise
		// the perfermance(it is not quite nessesary)
		user.setChosenToIntiate();
	}

	public void userStartFacetime(GameMessage message) {
		// Set the matched pair's status to START_CHATTING respectively .
		FacetimeUser user = userManager.findUserById(message.getUserId());
		FacetimeUser matchedUser = user.getMatchedUser();
		user.setStatus(FacetimeUser.START_CHATTING);
		matchedUser.setStatus(FacetimeUser.START_CHATTING);

		// We shall remove the user and the matchedUser from userList.
		// Although we could not know the matchedUser's Facetime status,
		// however since we have set both to START_CHATTING, it is useless to
		// remain it on the userList,which is used to hold the chat-applying
		// users.
		userManager.removeUser(user);
		userManager.removeUser(matchedUser);
	}

	// In case of user canceling the connection after applying for a chat.
	public void cleanUserOnChannel(ChannelStateEvent e) {
		FacetimeUser user = userManager.findUserByChannel(e.getChannel());
		userManager.removeUser(user);
	}

}// end of class

