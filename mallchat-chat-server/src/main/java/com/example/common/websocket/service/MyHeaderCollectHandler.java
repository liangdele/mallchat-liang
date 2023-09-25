package com.example.common.websocket.service;

import cn.hutool.core.net.url.UrlBuilder;
import com.example.common.websocket.NettyUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Optional;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">liang</a>
 * Date: 2023-09-03
 */
public class MyHeaderCollectHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final HttpObject httpObject = (HttpObject) msg;
        //使用子协议是一种方案，但是不适合，因为netty不建议这么做
/*
        if (httpObject instanceof HttpRequest) {
            final HttpRequest req = (HttpRequest) httpObject;
            String token = req.headers().get("Sec-WebSocket-Protocol");
             Attribute<Object> token1 = ctx.channel().attr(AttributeKey.valueOf("token"));
            token1.set(token);
            final WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    req.getUri(),
                    token, false);
            final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                ctx.pipeline().remove(this);

                final ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) {
                        if (!future.isSuccess()) {
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            // Kept for compatibility
                            ctx.fireUserEventTriggered(
                                    WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                        }
                    }
                });
            }
        } else
            ctx.fireChannelRead(msg);*/

        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());
            Optional<String> tokenOptional = Optional.of(urlBuilder)
                    .map(UrlBuilder::getQuery)
                    .map(k -> k.get("token"))
                    .map(CharSequence::toString);
            //如果token存在
            tokenOptional.ifPresent(s -> NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, s));
            request.setUri(urlBuilder.getPath().toString());
        }
        ctx.fireChannelRead(msg);
    }

}
