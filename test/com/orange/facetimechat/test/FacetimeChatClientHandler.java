package com.orange.facetimechat.test;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.orange.network.game.protocol.message.GameMessageProtos;
import com.orange.network.game.protocol.message.GameMessageProtos.GameMessage;

public class FacetimeChatClientHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = Logger
			.getLogger(FacetimeChatClientHandler.class.getName());
	
	final FacetimeTestService testService;

	public FacetimeChatClientHandler(FacetimeTestService testService) {
		super();
		this.testService = testService;
	}

	@Override
	public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) {

		try {
			logger.debug(e.toString());
			super.handleUpstream(ctx, e);
		} catch (Exception exception) {
			logger.error("<handleUpstream> catch unexpected exception at "
					+ e.getChannel().toString() + ", cause="+exception.toString(),exception);
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		GameMessage message = (GameMessageProtos.GameMessage) e.getMessage();
		logger.info("<messageReceiver> = " + message);
		switch (message.getCommand()) {
		case FACETIME_CHAT_RESPONSE:
			StatisticService.getInstance().addFacetimeMatch(testService.getUserId(), 
					message.getFacetimeChatResponse().getUser(0).getUserId());
			if (message.getFacetimeChatResponse().getChosenToInitiate()) {
				testService.simulateFacetimeStartRequest(message);
			}
			// disconnect to release the channel and complete the simulation
//			e.getChannel().disconnect();
//			e.getChannel().close();
			break;
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		logger.error("<exceptionCaught> catch unexpected exception at "
				+ e.getChannel().toString() + ", cause="
				+ e.getCause().getMessage(), e.getCause());
		e.getChannel().close();
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) {
		logger.info("<channelDisconnected> channel = "
				+ e.getChannel().toString());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) {
		logger.info("<channelClosed> channel = " + e.getChannel().toString());
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		logger
				.info("<channelConnected> channel = "
						+ e.getChannel().toString());
		testService.setChannel(e.getChannel());
		testService.simulateMatchRequest();
	}
}
