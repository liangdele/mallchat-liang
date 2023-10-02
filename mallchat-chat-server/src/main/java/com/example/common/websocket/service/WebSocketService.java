package com.example.common.websocket.service;

import com.example.common.websocket.domain.vo.req.WSBaseReq;
import com.example.common.websocket.domain.vo.resp.WSBaseResp;
import io.netty.channel.Channel;

public interface WebSocketService {
    void connect(Channel channel);

    void handleLoginReq(Channel channel);

    void remove(Channel channel);

    void scanLoginSuccess(Integer code, Long uid);

    void waitAuthorize(Integer code);

    void authorize(Channel channel, String data);

    void sendMsgToAll(WSBaseResp<?> wsBaseResp);
}
