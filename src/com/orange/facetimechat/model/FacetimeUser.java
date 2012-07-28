package com.orange.facetimechat.model;

import org.jboss.netty.channel.Channel;

import com.orange.network.game.protocol.message.GameMessageProtos.GameMessage;
import com.orange.network.game.protocol.model.GameBasicProtos.PBGameUser;

public class FacetimeUser {

	public final static int WAIT_MATCHING = 1; // Wait for matching,the initial status.
	public final static int MATCHED = 2;	// Found matchup, set this.
	public final static int START_CHATTING = 3; // After sending Ftchat response, set this status.
	
	// Required parameters
	private final PBGameUser user;
	private volatile long lastRequestTime;
	private volatile int status;
	private final Channel channel;
	private FacetimeUser matchedUser;
	volatile private boolean chosenToInitiate; // chosen to initiate the Facetime Chatting?
	volatile private boolean sentFacetimeResponse; // has sent FacetimeChat response?

	// Optional parameters
	private boolean findByGender;
	private boolean chatGender;
	
	// Use this inner class to initiate the FacetimeUser instance.
	public static class Builder {
		// Required parameters
		private final PBGameUser user;
		private final long lastRequestTime;
		private final int status;
		private final Channel channel;
		private final FacetimeUser matchedUser;
		private final boolean chosenToInitiate;
		private final boolean sentFacetimeResponse;
			
		// Optional parameters
		private boolean findByGender;
		private boolean chatGender ;
		
		public Builder(PBGameUser user,Channel channel) {
			this.user = user;
			this.lastRequestTime = System.currentTimeMillis();
			this.status  = WAIT_MATCHING;
			this.channel = channel;
			this.matchedUser = null;
			this.chosenToInitiate = false;
			this.sentFacetimeResponse = false;
		}
		
		public Builder  setChatGender(GameMessage message) {
			if (message.getFacetimeChatRequest().hasChatGender() == true) {
				this.findByGender = true;
				this.chatGender = message.getFacetimeChatRequest().getChatGender();
			}
			else {
				this.findByGender = false;
				this.chatGender = false; // It won't be used. set it to false by convention.
			}
			
			return this;
		}
		
		public FacetimeUser build() {
			return new FacetimeUser(this);
		}
	}
	
	private FacetimeUser(Builder builder) {
		user = builder.user;
		lastRequestTime = builder.lastRequestTime;
		status  = builder.status;
		channel = builder.channel;
		matchedUser = builder.matchedUser;
		chosenToInitiate = builder.chosenToInitiate;
		sentFacetimeResponse = builder.sentFacetimeResponse;
		findByGender = builder.findByGender;
		chatGender = builder.chatGender;
	}
	
	/* Below starts auxiliary methods */
	
	public String toString() {
		return user.getUserId();
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public int getStatus() {
		return status;
	}

	synchronized public void setStatus(int status) {
		this.status = status;
	}

	public PBGameUser getUser() {
		return user;
	}
	
	public boolean getChatGender() {
		return chatGender;
	}
	
	public boolean getUserGender() {
		return user.getGender();
	}

	public synchronized boolean isMatched() {
		// Actually the START_CHATTING *NEED NOT* check!
		// Because when it is set to START_CHATTING, it will be
		// removed from wating-for-match-queue(See userStartFacetime() in ChatMatchService.java)
		// However we could not predict the thread's schedule, and 
		// the Set-status-operation and Remove-operation are not atomic(it needn't be actually)
		// So it is safe to check it. 
		if (status == MATCHED || status == START_CHATTING)
			return true;
		return false; // status == WAIT_MATCHING
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isMyself(String userId) {
		if (userId == null)
			return false;
		return userId.equals(user.getUserId());

	}

	// Two methods about setting the matched user.
	public synchronized void setMatchedUser(FacetimeUser matchedUser) {
		this.matchedUser = matchedUser;
	}

	public synchronized FacetimeUser getMatchedUser() {
		return matchedUser;
	}

	//  Two methods about whether user is chosen to initiate chatting
	public void setChosenToIntiate() {
		// Must check if the matched user is chosen to initiate.
		if (this.matchedUser.isChosenToInitiate() == false)
			this.chosenToInitiate = true;
	}

	public boolean isChosenToInitiate() {
		return chosenToInitiate;
	}


	//  Two methods about whether sent user a FacetimeResponse
	public void setSentFacetimeResponse() {
		this.sentFacetimeResponse = true;
	}

	synchronized  public boolean getSentFacetimeResponse() {
		return sentFacetimeResponse;
	}


	public boolean isFindByGender() {
		return findByGender;
	}

}
