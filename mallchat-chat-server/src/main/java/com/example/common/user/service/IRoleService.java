package com.example.common.user.service;

import com.example.common.user.domain.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.user.domain.enums.RoletEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author <a href="https://github.com/liangdele">liangdele</a>
 * @since 2023-10-02
 */
public interface IRoleService  {
    /**
     * 判断用户是否有某个权限，临时写法
     * @param uid
     * @param roletEnum
     * @return
     */
    boolean hasPower(Long uid, RoletEnum roletEnum);
}
