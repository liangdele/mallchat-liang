package com.example.common.user.dao;

import com.example.common.user.domain.entity.User;
import com.example.common.user.mapper.UserMapper;
import com.example.common.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/zongzibinbin">abin</a>
 * @since 2023-09-10
 */
@Service
public class UserDao extends ServiceImpl<UserMapper, User> implements IUserService {

}
