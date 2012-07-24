package com.orange.facetimechat.server;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import com.orange.facetimechat.model.FacetimeUser;
import com.orange.facetimechat.model.FacetimeUserManager;
import com.orange.game.model.manager.UserManager;


// For idle state handle,
// If the client keep silent for 300 seconds since the channel connected
// (the server side readerIdleTime event triggerd), then we should just 
// close the channel.
public class FacetimeChatIdleStateHandler extends IdleStateAwareChannelHandler {
	
	private static final Logger logger = Logger.getLogger(FacetimeChatIdleStateHandler.class
			.getName()); 
	FacetimeUserManager userManager = FacetimeUserManager.getInstance();
	
   @Override
   // When read idle time expires, we simply close the channel,
   // except the waiting-matching user and the non-initiate-FacetimeChat user.
   public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) {
	   if (e.getState() == IdleState.READER_IDLE) {
		   FacetimeUser user = userManager.findUserByChannel(e.getChannel());
		   if (user.getStatus() != FacetimeUser.WAIT_MATCHING && user.isChosenToInitiate() == true)
		   	{ 
			   logger.info("<channelIdle>" + userManager.findUserByChannel(e.getChannel())
					   + " idles too long time...~_~\n");
			   e.getChannel().close();
		   	}
         }
     }
 
}

