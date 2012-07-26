package com.orange.facetimechat.model;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;
import org.antlr.grammar.v3.ANTLRv3Parser.range_return;
import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import com.orange.network.game.protocol.model.GameBasicProtos.PBGameUser;

public class FacetimeUser {

	public final static int WAIT_MATCHING = 1;
	public final static int MATCHED = 2;
	public final static int START_CHATTING = 3;
	
	final PBGameUser user;
	volatile long lastRequestTime;
	volatile int status;
	final Channel channel;
	private FacetimeUser matchedUser = null;

	// Granted to initiate the chatting? Default is false(No!).
	// We should set it when send FacetimeChatresponse.
	volatile private boolean chosenToInitiate = false;
	// Has sent a response?
	volatile private boolean sentFacetimeResponse = false;

	public FacetimeUser(PBGameUser user, Channel channel) {
		this.user = user;
		this.lastRequestTime = System.currentTimeMillis();
		this.status = WAIT_MATCHING;
		this.channel = channel;
	}
	
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

	public synchronized boolean isMatched() {
		// Actually the START_CHATTING *NEED NOT* check!
		// Because when it is set to START_CHATTING, it will be
		// removed from userList(See userStartFacetime() in ChatMatchService.java)
		// However we could not predict the thread's schedule, and 
		// the Set-status-action and Remove-action are not atomic(it needn't)
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

	public boolean getSentFacetimeResponse() {
		return sentFacetimeResponse;
	}

}
