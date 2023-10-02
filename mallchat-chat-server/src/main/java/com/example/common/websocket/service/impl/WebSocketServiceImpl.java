package com.example.common.websocket.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.example.common.common.config.ThreadPoolConfig;
import com.example.common.common.event.UserOnlineEvent;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.IpInfo;
import com.example.common.user.domain.entity.User;
import com.example.common.user.domain.enums.RoletEnum;
import com.example.common.user.service.IRoleService;
import com.example.common.user.service.impl.LoginServiceImpl;
import com.example.common.user.service.impl.UserServiceImpl;
import com.example.common.websocket.NettyUtil;
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
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import lombok.SneakyThrows;
import lombok.val;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门管理WebSocket的逻辑，包括推拉
 */
@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Autowired
    private UserDao userDao;
    @Autowired
    @Lazy
    private WxMpService wxMpService;
    @Autowired
    private LoginServiceImpl loginService;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private IRoleService roleService;
    @Autowired
    @Qualifier(ThreadPoolConfig.WS_EXECUTOR)
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
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

    @Override
    public void scanLoginSuccess(Integer code, Long uid) {
        //确认链接在机器上
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        User user = userDao.getById(uid);
        //移除code
        WAIT_LOGIN_MAP.invalidate(code);
        //调用登录模块获取token
        String token = loginService.login(uid);
        //用户登录
        loginSuccess(channel, user, token);
    }

    @Override
    public void waitAuthorize(Integer code) {
        Channel channel = WAIT_LOGIN_MAP.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return;
        }
        sendMsg(channel, WebSocketAdapter.buildWaitAuthorizeResp());
    }

    @Override
    public void authorize(Channel channel, String token) {
        Long uid = loginService.getValidUid(token);
        if (Objects.nonNull(uid)) {
            User user = userDao.getById(uid);
            loginSuccess(channel, user, token);

        } else {
            sendMsg(channel, WebSocketAdapter.buildInvalidTokenResp());
        }
    }

    @Override
    public void sendMsgToAll(WSBaseResp<?> wsBaseResp) {
        ONLINE_WS_MAP.forEach((channel, wsChannelExtraDTO) -> {
            threadPoolTaskExecutor.execute(()->{
                sendMsg(channel, wsBaseResp);
            });
        });
    }

    private void loginSuccess(Channel channel, User user, String token) {
        //保存channel的对应uid
        WSChannelExtraDTO wsChannelExtraDTO = ONLINE_WS_MAP.get(channel);
        wsChannelExtraDTO.setUid(user.getId());
        //推送成功消息
        sendMsg(channel, WebSocketAdapter.buildResp(user, token, roleService.hasPower(user.getId(), RoletEnum.CHAT_MANAGER)));
        //用户上线成功的事件
        user.setLastOptTime(new Date());
        user.refreshIp(NettyUtil.getAttr(channel, NettyUtil.IP));
        applicationEventPublisher.publishEvent(new UserOnlineEvent(this, user));
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
