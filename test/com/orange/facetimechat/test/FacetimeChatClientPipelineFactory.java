package com.orange.facetimechat.test;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import com.orange.network.game.protocol.message.GameMessageProtos;

public class FacetimeChatClientPipelineFactory implements
		ChannelPipelineFactory {
	
	final FacetimeTestService testService;

	public FacetimeChatClientPipelineFactory(FacetimeTestService testService) {
		super();
		this.testService = testService;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline p = Channels.pipeline();
		p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
		p.addLast("protobufDecoder", new ProtobufDecoder(GameMessageProtos.GameMessage.getDefaultInstance()));
		 
		p.addLast("frameEncoder", new LengthFieldPrepender(4));
		p.addLast("protobufEncoder", new ProtobufEncoder());
		 
		p.addLast("handler", new FacetimeChatClientHandler(testService));
		return p;	
	}

}
