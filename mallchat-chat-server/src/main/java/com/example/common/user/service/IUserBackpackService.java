package com.example.common.user.service;

import com.example.common.user.domain.entity.UserBackpack;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.user.domain.enums.IdempotentEnum;

/**
 * <p>
 * 用户背包表 服务类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-09-18
 */
public interface IUserBackpackService {
    /**
     * 用户发放一个物品
     *
     * @param uid            用户id
     * @param ItemId         物品id
     * @param idempotentEnum 幂等枚举
     * @param businessId     幂等唯一标识
     */
    void acquireItem(Long uid, Long ItemId, IdempotentEnum idempotentEnum, String businessId);
}
