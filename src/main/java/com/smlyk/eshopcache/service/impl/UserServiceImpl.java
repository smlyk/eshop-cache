package com.smlyk.eshopcache.service.impl;

import com.smlyk.eshopcache.model.User;
import com.smlyk.eshopcache.mapper.UserMapper;
import com.smlyk.eshopcache.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author always
 * @since 2021-01-04
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public int updateUser(User user) {
        return baseMapper.updateById(user);
    }

    @Override
    public User getUser(Integer id) {
        return baseMapper.selectById(id);
    }
}
