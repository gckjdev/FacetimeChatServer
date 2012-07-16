package com.orange.facetimechat.model;

import com.orange.network.game.protocol.model.GameBasicProtos.PBGameUser;

public class FacetimeUser {

	public final static int WAIT_MATCHING = 1;	
	
	final PBGameUser user;
	volatile long lastRequestTime;
	volatile int status;
	
	public FacetimeUser(PBGameUser user){
		this.user = user;
		this.lastRequestTime = System.currentTimeMillis();
	}

	public long getLastRequestTime() {
		return lastRequestTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public PBGameUser getUser() {
		return user;
	}
	
	
}
