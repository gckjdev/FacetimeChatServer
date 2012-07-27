package com.orange.facetimechat.server;

import java.nio.channels.Channel;

import org.antlr.grammar.v3.ANTLRv3Parser.throwsSpec_return;
import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
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

public class FacetimeChatServerHandler extends SimpleChannelUpstreamHandler  {

	private static final Logger logger = Logger.getLogger(FacetimeChatServerHandler.class
			.getName()); 
	private final ChatMatchService chatMatchService = ChatMatchService.getInstance();
	
	
	private static final FacetimeChatServerHandler facetimeChatServerHandler = new FacetimeChatServerHandler();
	public static FacetimeChatServerHandler getInstance() {
		return facetimeChatServerHandler;
	}
	
	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e){
		
		try {
			logger.debug(e.toString());
			super.handleUpstream(ctx, e);
		} catch (Exception exception) {
			logger.error("<handleUpstream> catch unexpected exception at " + e.getChannel().toString()
					+ ", cause="+exception.toString(),exception);			
		}				
	}
	
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
				
		GameMessage message = (GameMessageProtos.GameMessage)e.getMessage();	
		org.jboss.netty.channel.Channel channel = e.getChannel();
		
		try {
			checkMessageValidity(channel,message);
		} catch (NullPointerException exception) {
			logger.error("<messageReceived> an unexpected FacetimeChatRequest received!"
					+ "Close the channel !!!");
			channel.close();
		}
		
		logger.info("<messageReceived> " + message);
		switch (message.getCommand()){
			case FACETIME_CHAT_REQUEST:
				ChatMatchService.getInstance().matchUserChatRequest(message, channel);
				break;
			case FACETIME_CHAT_START:
				ChatMatchService.getInstance().userStartFacetime(message);
				break;
		}		
		
	}
	
	private void checkMessageValidity(org.jboss.netty.channel.Channel channel,GameMessage message) 
	throws NullPointerException 
	{
		
		if (message.getFacetimeChatRequest() == null ||
				message.getFacetimeChatRequest().getUser() == null)
			throw new NullPointerException("<messageReceived> invalid message received!");
	}
	
	@Override
	public void exceptionCaught( ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("<exceptionCaught> catch unexpected exception at " + e.getChannel().toString() + ", cause=" + e.getCause().getMessage(), e.getCause());
		e.getChannel().close();
	}				
	
	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e){
		chatMatchService.cleanUserOnChannel(e.getChannel());
		logger.info("<channelDisconnected> channel = " + e.getChannel().toString());
	  	
	}
	
	@Override
	public void channelClosed(ChannelHandlerContext ctx,
            ChannelStateEvent e){
		// In case of user canceling the connection after aplying for a match.
		chatMatchService.cleanUserOnChannel(e.getChannel());
		logger.info("<channelClosed> channel = " + e.getChannel().toString());
	}
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx,
            ChannelStateEvent e){
		logger.info("<channelConnected> channel = " + e.getChannel().toString());	
	}	
	
	
}