package com.microhttp.sink;

import java.util.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Netty based Http(s) Server 
 * @author skalaise
 *
 */
public class NettyServer {

	private final static Logger _log = Logger.getLogger(NettyServer.class.getName());
    
	/**
	 * Create Netty Bootstrp and bind it to port 
	 * @param config
	 * @throws Exception
	 */
	public void start( NettyServerConfig config ) throws Exception {
		
		// TD :DO - Test SSL by obtaining Cert from authority 
		final SslContext sslCtx;
        if (config.isSslEnabled()) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
            // make sure to listen https port
            config.setListenPort(8443); // override to https port though user supplied input 
        } else {
            sslCtx = null;
        }
        
		final EventLoopGroup bossGroup;
	    final EventLoopGroup workerGroup;
	    
	    if (Epoll.isAvailable()) {
	    	_log.info("Event Loop: epoll");
            bossGroup = new EpollEventLoopGroup(config.getBossGroupThreads());
            workerGroup = new EpollEventLoopGroup();
        } else {
        	_log.info("Event Loop: NIO");
            bossGroup = new NioEventLoopGroup(config.getBossGroupThreads());
            workerGroup = new NioEventLoopGroup();
        }
	    
	    try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(bossGroup, workerGroup)
             .option(ChannelOption.SO_REUSEADDR, true);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.childHandler(new NettyServerInitializer(sslCtx));

            if (Epoll.isAvailable()) {
                bootstrap.channel(EpollServerSocketChannel.class);
            } else {
                bootstrap.channel(NioServerSocketChannel.class);
            }
            // Bind and start to accept incoming connections.
            ChannelFuture future = bootstrap.bind(config.getListenPort()).sync();
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
	}
}
