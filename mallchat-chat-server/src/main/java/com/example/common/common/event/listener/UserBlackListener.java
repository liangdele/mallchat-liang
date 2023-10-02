package com.example.common.common.event.listener;

import com.example.common.common.event.UserBlackEvent;
import com.example.common.common.event.UserOnlineEvent;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.User;
import com.example.common.user.domain.enums.UserActiveStatusEnum;
import com.example.common.user.service.IpService;
import com.example.common.user.service.cache.UserCache;
import com.example.common.websocket.service.WebSocketService;
import com.example.common.websocket.service.adapter.WebSocketAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserBlackListener {
    @Autowired
    private IpService ipService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserCache userCache;

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void sendMsg(UserOnlineEvent event) {
        final User user = event.getUser();
        webSocketService.sendMsgToAll(WebSocketAdapter.buildBlackReq(user));
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void changeUserStatus(UserOnlineEvent event) {
        userDao.invalidUid(event.getUser().getId());
    }

    @Async
    @TransactionalEventListener(classes = UserBlackEvent.class, phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void evictCache(UserOnlineEvent event) {
        userCache.clearBlackMap();
    }
}
