package com.microhttp.sink;


import com.microhttp.sink.handler.DataHandler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

/**
 * ChannelInitializer 
 * @author skalaise
 *
 */
public class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
	
	private final SslContext sslCtx;

    public NettyServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        if (sslCtx != null) {
            p.addFirst(sslCtx.newHandler(ch.alloc()));
        }
        p.addLast(new HttpServerCodec());
        p.addLast("aggregator", new HttpObjectAggregator(100000)); // 100 KB max size 
        p.addLast(new DataHandler());
    }

}
