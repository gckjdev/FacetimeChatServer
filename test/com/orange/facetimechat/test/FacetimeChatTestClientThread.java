package com.orange.facetimechat.test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.antlr.grammar.v3.ANTLRv3Parser.finallyClause_return;
import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import antlr.collections.List;

class StartFacetimeChatTestClient {
	
	
	public StartFacetimeChatTestClient() {
		// Parse options.
        String host = "127.0.0.1";
        int port = 8191;
        // Configure the client.
        
        ClientBootstrap bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the event pipeline factory.
        FacetimeTestService testService = new FacetimeTestService();
        bootstrap.setPipelineFactory(new FacetimeChatClientPipelineFactory(testService));
        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));        
        
        // Wait until the connection is closed or the connection attempt fails.
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        // Shut down thread pools to exit.
        bootstrap.releaseExternalResources();	
        }
}

public class FacetimeChatTestClientThread extends Thread {
	private static final Logger LOGGER = Logger
			.getLogger(FacetimeChatTestClientThread.class.getName());
	
	
	public String toString() {
		return "#" + getName(); 
	}
	
	public FacetimeChatTestClientThread() {
		start();
	}
	
	public  void run() {
		LOGGER.info("Starting a new FacetimeChatTestClient " + this);
		new StartFacetimeChatTestClient();
	}
		
	public static void main(String[] args) {
		
		for (int i = 0; i < 5; i++)
			new FacetimeChatTestClientThread();

	}
}