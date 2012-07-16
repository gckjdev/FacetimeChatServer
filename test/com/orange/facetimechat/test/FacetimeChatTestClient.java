package com.orange.facetimechat.test;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

public class FacetimeChatTestClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Parse options.
        String host = "127.0.0.1";
        int port = 9800;
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
        bootstrap.releaseExternalResources();	}

}
