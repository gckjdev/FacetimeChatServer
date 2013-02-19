package com.orange.facetimechat.server;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.handler.codec.protobuf.ProtobufDecoder;
import org.jboss.netty.handler.codec.protobuf.ProtobufEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;
import com.orange.network.game.protocol.message.GameMessageProtos;

public class FactimeChatServerPipelineFactory implements ChannelPipelineFactory {
	
	private HashedWheelTimer timer;
	public FactimeChatServerPipelineFactory(HashedWheelTimer timer) {
		this.timer = timer;
	}
	private static int EXPIRE_TIME_SECONDS = 300; // Modify this to set the idle time, we set to 300(s).
	private static int READ_IDLE_TIME_SECONDS = EXPIRE_TIME_SECONDS; 
	private static int WRITE_IDLE_TIME_SECONDS = 0; // Write idle time. Not used here.  
	private static int ALL_IDLE_TIME_SECONDS = 0;   // Read/write idle time. Not used here. 
	
	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline p = Channels.pipeline();
		
		
		p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
		p.addLast("protobufDecoder", new ProtobufDecoder(GameMessageProtos.GameMessage.getDefaultInstance()));
		 
		p.addLast("frameEncoder", new LengthFieldPrepender(4));
		p.addLast("protobufEncoder", new ProtobufEncoder());
		 
		
		p.addLast("handle", new FacetimeChatServerHandler());
		
		/** For idle state handler
		 * 
		 * idleStateHandler handles an idle event(read idle, write idle, or both),
		 * and triggers an idleStateEvent,
		 * which is then handled by idleStateChannelHandler, we can do any 
		 * cleanup there .
		 */
		p.addLast("idleStateHandler", new IdleStateHandler(timer, READ_IDLE_TIME_SECONDS,
				WRITE_IDLE_TIME_SECONDS,ALL_IDLE_TIME_SECONDS));		
 		p.addLast("idleStateChannelHandler", new FacetimeChatIdleStateChannelHandler());
		
		return p;	
	}

}
