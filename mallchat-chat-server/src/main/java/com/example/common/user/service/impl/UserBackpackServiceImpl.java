package com.example.common.user.service.impl;

import com.example.common.common.annotation.RedissonLock;
import com.example.common.common.domain.enums.YesOrNoEnum;
import com.example.common.common.service.LockService;
import com.example.common.common.utils.AssertUtil;
import com.example.common.user.dao.UserBackpackDao;
import com.example.common.user.domain.entity.UserBackpack;
import com.example.common.user.domain.enums.IdempotentEnum;
import com.example.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {
    @Autowired
    private LockService lockService;
    @Autowired
    private UserBackpackDao userBackpackDao;
    @Autowired
    @Lazy
    private UserBackpackServiceImpl userBackpackService;

    @Override
    public void acquireItem(Long uid, Long ItemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(ItemId, idempotentEnum, businessId);
        userBackpackService.doAcquireItem(uid, ItemId, idempotent);
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        //幂等逻辑
        UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.NO.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
