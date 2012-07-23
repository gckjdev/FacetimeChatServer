package com.orange.facetimechat.server;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.orange.facetimechat.service.ChatMatchService;
import com.orange.network.game.protocol.message.GameMessageProtos;
import com.orange.network.game.protocol.message.GameMessageProtos.GameMessage;

public class FacetimeChatServerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger.getLogger(FacetimeChatServerHandler.class
			.getName()); 
	private final ChatMatchService chatMatchService = ChatMatchService.getInstance();
	
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e){
		
		try {
			logger.debug(e.toString());
			super.handleUpstream(ctx, e);
		} catch (Exception exception) {
			logger.error("<handleUpstream> catch unexpected exception at " + e.getChannel().toString()
					+ ", cause=", exception.getCause());			
		}				
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
				
		GameMessage message = (GameMessageProtos.GameMessage)e.getMessage();	
		logger.info("<messageReceived> " + message);
		switch (message.getCommand()){
			case FACETIME_CHAT_REQUEST:
				ChatMatchService.getInstance().matchUserChatRequest(message, e.getChannel());
				break;
			case FACETIME_CHAT_START:
				ChatMatchService.getInstance().userStartFacetime(message);
				break;
		}		
		
	}
	
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("<exceptionCaught> catch unexpected exception at " + e.getChannel().toString() + ", cause=" + e.getCause().getMessage(), e.getCause());
		e.getChannel().close();
	}				
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e){
		chatMatchService.cleanUserOnChannel(e);
		logger.info("<channelDisconnected> channel = " + e.getChannel().toString());
	  	
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx,
            ChannelStateEvent e){
		// In case of user canceling the connection after aplying for a match.
		chatMatchService.cleanUserOnChannel(e);
		logger.info("<channelClosed> channel = " + e.getChannel().toString());
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx,
            ChannelStateEvent e){
		logger.info("<channelConnected> channel = " + e.getChannel().toString());	

	}	
}
