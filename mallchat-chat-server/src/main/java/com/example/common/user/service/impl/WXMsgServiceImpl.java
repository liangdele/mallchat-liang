package com.example.common.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.User;
import com.example.common.user.service.WXMsgService;
import com.example.common.user.service.adapter.TextBuilder;
import com.example.common.user.service.adapter.UserAdapter;
import com.example.common.websocket.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WXMsgServiceImpl implements WXMsgService {
    @Autowired
    private WebSocketService webSocketService;
    /**
     * openid和登录code的关系
     */
    private static final ConcurrentHashMap<String, Integer> WAIT_AUTHORIZE_MAP = new ConcurrentHashMap<>();
    @Value("${wx.mp.callback}")
    private String callback;
    private final static String URL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";

    @Autowired
    private UserDao userDao;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    @Lazy
    private WxMpService wxMpService;

    @Override
    public WxMpXmlOutMessage scan(WxMpXmlMessage wxMpXmlMessage) {
        String openId = wxMpXmlMessage.getFromUser();
        Integer code = getEventKey(wxMpXmlMessage);
        if (Objects.isNull(code)) {
            return null;
        }
        User user = userDao.getByOpenId(openId);
        boolean registered = Objects.nonNull(user);
        boolean authorized = registered && StrUtil.isNotBlank(user.getAvatar());
        //用户已经注册并授权
        if (registered && authorized) {
            //登录成功逻辑 通过code找到channel给channel推送消息
            webSocketService.scanLoginSuccess(code,user.getId());
            return null;
        }
        //用户未注册，就先注册
        if (!registered) {
            User insert = UserAdapter.buildUserSave(openId);
            userService.register(insert);
        }

        //推送链接让用户
        WAIT_AUTHORIZE_MAP.put(openId, code);
        webSocketService.waitAuthorize(code);
        // 扫码事件处理
        String authorizeUrl = String.format(URL, wxMpService.getWxMpConfigStorage().getAppId(), URLEncoder.encode(callback + "/wx/portal/public/callBack"));
        return TextBuilder.build("请点击登录:<a href=\"" + authorizeUrl + "\">登录</a>", wxMpXmlMessage);
    }

    private Integer getEventKey(WxMpXmlMessage wxMpXmlMessage) {
        try {
            String eventKey = wxMpXmlMessage.getEventKey();
            String code = eventKey.replace("qrcscen_", "");
            return Integer.parseInt(code);
        } catch (Exception e) {
            log.error("getEvent error eventKey:{}", wxMpXmlMessage.getEventKey(), e);
            return null;
        }
    }

    @Override
    public void authorize(WxOAuth2UserInfo userInfo) {
        String openid = userInfo.getOpenid();
        User user = userDao.getByOpenId(openid);
        //更新用户信息
        if (StrUtil.isBlank(user.getAvatar())) {
            fillUserInfo(user.getId(), userInfo);
        }
        //通过code，找到用户channel，进行登录
        Integer code = WAIT_AUTHORIZE_MAP.remove(openid);

    }

    private void fillUserInfo(Long uid, WxOAuth2UserInfo userInfo) {
        User user = UserAdapter.buildAuthorizeUser(uid, userInfo);
        userDao.updateById(user);
    }
}
