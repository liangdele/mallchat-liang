package com.example.common.user.dao;

import com.example.common.user.domain.entity.Black;
import com.example.common.user.mapper.BlackMapper;
import com.example.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://github.com/liangdele">liangdele</a>
 * @since 2023-10-02
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
