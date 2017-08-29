package com.microhttp.sink.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

/**
 * Data Handler - Sharable 
 * Responsibilites 
 * 1) Receive Full HttpRequest
 * 2) Read Request Contents
 * 3) Write content to destination( Kafka,Cassandra )
 * 4) Respond to the request 
 * 
 * @author senthilec566
 *
 */
@Sharable
public class DataHandler extends ChannelInboundHandlerAdapter {
	// logger
	private final static Logger _log = Logger.getLogger(DataHandler.class.getName());
    
    @Override
    public void channelReadComplete( final ChannelHandlerContext ctx ) {
        ctx.flush();
    }
    
    /**
     * Read Channel API 
     */
	@Override
    public void channelRead( final ChannelHandlerContext ctx, Object msg) throws Exception {
		
        if (msg instanceof FullHttpRequest) {
            final FullHttpRequest fReq = (FullHttpRequest) msg;
            if (HttpHeaders.is100ContinueExpected(fReq)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }
            String data = readIncomingData(fReq);
        	if ( data == null ) {
        		writeBadReqResponse(ctx);
        	}
        	boolean processed = DataProcessingHandler.processIncomingData(data);
            data = null;
            writeResponse(fReq, ctx, processed);
        }
    }

	/**
	 * Handles excpetion in channel 
	 */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	_log.log(Level.SEVERE, cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
    
    /**
     * Read incoming request POST body 
     * @param fReq
     * @return
     */
    public String readIncomingData( final FullHttpRequest fReq ){
    	String data = null;
    	final ByteBuf buf = fReq.content();
    	if( buf == null ){
    		return null;
    	}
    	try{
    		if (buf.isReadable()) 
    			data = buf.toString();
    	}catch(Exception e ){
    		_log.log(Level.SEVERE, e.getMessage());
    	}finally {
    		ReferenceCountUtil.release(buf);
    	}
        return data;
    }
    
    /**
     * Respond to client either success or failure 
     * @param fReq
     * @param ctx
     * @param success
     */
    public void writeResponse( final FullHttpRequest fReq , final ChannelHandlerContext ctx , final boolean success ){
    	boolean keepAlive = HttpHeaders.isKeepAlive(fReq);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK , Unpooled.wrappedBuffer("SUCCESS".getBytes()) );
        if( !success ) // maximum case would be success :-) 
        	response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK , Unpooled.wrappedBuffer("FAILURE".getBytes()) );
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.write(response , ctx.voidPromise()).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            ctx.write(response, ctx.voidPromise());
        }
    }
    
    /**
     * Initimate Client that bad request is received  
     * @param ctx
     */
    public void writeBadReqResponse(final ChannelHandlerContext ctx){
    	FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST , Unpooled.wrappedBuffer("BADREQ".getBytes()) );
    	response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, response.content().readableBytes());
        ctx.write(response, ctx.voidPromise());
    }
}
