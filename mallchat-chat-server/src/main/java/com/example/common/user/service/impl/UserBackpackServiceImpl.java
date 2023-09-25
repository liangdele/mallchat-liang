package com.example.common.user.service.impl;

import com.example.common.common.domain.enums.YesOrNoEnum;
import com.example.common.common.utils.AssertUtil;
import com.example.common.user.dao.UserBackpackDao;
import com.example.common.user.domain.entity.UserBackpack;
import com.example.common.user.domain.enums.IdempotentEnum;
import com.example.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private UserBackpackDao userBackpackDao;

    @Override
    public void acquireItem(Long uid, Long ItemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(ItemId, idempotentEnum, businessId);
        RLock lock = redissonClient.getLock("acquireItem:" + idempotent);
        boolean b = lock.tryLock();
        AssertUtil.isTrue(b, "请勿重复操作");
        try {
            //幂等逻辑
            UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
            if (Objects.nonNull(userBackpack)) {
                return;
            }
            //业务的检查

            //发放物品
            UserBackpack insert = userBackpack.builder()
                    .uid(uid)
                    .itemId(ItemId)
                    .status(YesOrNoEnum.NO.getStatus())
                    .idempotent(idempotent)
                    .build();
            userBackpackDao.save(insert);
        } finally {
            lock.unlock();
        }
    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
