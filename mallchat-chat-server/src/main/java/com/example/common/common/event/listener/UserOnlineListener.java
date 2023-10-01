package com.example.common.common.event.listener;

import com.example.common.common.event.UserOnlineEvent;
import com.example.common.common.event.UserRegisterEvent;
import com.example.common.user.dao.UserDao;
import com.example.common.user.domain.entity.User;
import com.example.common.user.domain.enums.IdempotentEnum;
import com.example.common.user.domain.enums.ItemEnum;
import com.example.common.user.domain.enums.UserActiveStatusEnum;
import com.example.common.user.service.IUserBackpackService;
import com.example.common.user.service.IpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class UserOnlineListener {
    @Autowired
    private IpService ipService;
    @Autowired
    private UserDao userDao;

    @Async
    @TransactionalEventListener(classes = UserOnlineEvent.class, phase = TransactionPhase.AFTER_COMMIT)
    public void saveDB(UserRegisterEvent event) {
        User user = event.getUser();
        User update = new User();
        update.setId(user.getId());
        update.setLastOptTime(user.getLastOptTime());
        update.setIpInfo(user.getIpInfo());
        update.setActiveStatus(UserActiveStatusEnum.ONLINE.getStatus());
        userDao.updateById(user);
        //用户ip详情的解析
        ipService.refreshIpDetailAsync(user.getId());
    }
}
