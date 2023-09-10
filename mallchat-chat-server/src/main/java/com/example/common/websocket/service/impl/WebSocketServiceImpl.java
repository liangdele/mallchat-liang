package com.example.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.example.common.websocket.domain.dto.WSChannelExtraDTO;
import com.example.common.websocket.domain.enums.WSRespTypeEnum;
import com.example.common.websocket.domain.vo.req.WSBaseReq;
import com.example.common.websocket.domain.vo.resp.WSBaseResp;
import com.example.common.websocket.domain.vo.resp.WSLoginUrl;
import com.example.common.websocket.service.WebSocketService;
import com.example.common.websocket.service.adapter.WebSocketAdapter;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.SneakyThrows;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门管理WebSocket的逻辑，包括推拉
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Autowired
    private WxMpService wxMpService;

    /**
     * 管理所有用户的连接，包含登录态和游客
     */
    private static final ConcurrentHashMap<Channel, WSChannelExtraDTO> ONLINE_WS_MAP = new ConcurrentHashMap<>();

    public static final int MAXIMUM_SIZE = 10000;
    public static final Duration DURATION = Duration.ofHours(1);
    /**
     * 临时保存登录code和channel
     */
    private static final Cache<Integer, Channel> WAIT_LOGIN_MAP = Caffeine.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(DURATION)
            .build();

    @Override
    public void connect(Channel channel) {
        ONLINE_WS_MAP.put(channel, new WSChannelExtraDTO());
    }

    @SneakyThrows
    @Override
    public void handleLoginReq(Channel channel) {
        //1.生成随机码
        Integer code = generateLoginCode(channel);
        //2.找微信申请带参二维码
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(code, (int) DURATION.getSeconds());
        //3.把码推送给前端
        sendMsg(channel, WebSocketAdapter.buildResp(wxMpQrCodeTicket));
    }

    @Override
    public void remove(Channel channel) {
        ONLINE_WS_MAP.remove(channel);
        //todo 用户下线等
    }

    private void sendMsg(Channel channel, WSBaseResp<?> resp) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(resp)));
    }

    private Integer generateLoginCode(Channel channel) {
        Integer code;
        do {
            code = RandomUtil.randomInt(Integer.MAX_VALUE);
        } while (Objects.nonNull(WAIT_LOGIN_MAP.asMap().putIfAbsent(code, channel)));

        return code;
    }
}
