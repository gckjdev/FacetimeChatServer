package com.orange.facetimechat.server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;

public class FacetimeChatServer {

	private static final Logger logger = Logger.getLogger(FacetimeChatServer.class
			.getName());
	
	public static int getPort() {
		String port = System.getProperty("server.port");
		if (port != null && !port.isEmpty()){
			return Integer.parseInt(port);
		}
		return 8191; // M13：2^13-1
	}
	
//	public static final int LANGUAGE_CHINESE = 1;
//	public static final int LANGUAGE_ENGLISH = 2;
//	public static int getLanguage() {
//		String lang = System.getProperty("config.lang");
//		if (lang != null && !lang.isEmpty()){
//			return Integer.parseInt(lang);
//		}
//		return LANGUAGE_CHINESE; // default
//	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		HashedWheelTimer timer = new HashedWheelTimer();
		
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
					Executors.newCachedThreadPool(),
					Executors.newCachedThreadPool()				
				));
		
		
		bootstrap.setPipelineFactory(new FactimeChatServerPipelineFactory(timer));
		
		bootstrap.bind(new InetSocketAddress(getPort()));
		logger.info("Start FaceTime Chat Server At Port "+getPort());
	}
}
