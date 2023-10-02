package com.example.common.user.dao;

import com.example.common.user.domain.entity.Role;
import com.example.common.user.mapper.RoleMapper;
import com.example.common.user.service.IRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liangdele">liangdele</a>
 * @since 2023-10-02
 */
@Service
public class RoleDao extends ServiceImpl<RoleMapper, Role> {

}
