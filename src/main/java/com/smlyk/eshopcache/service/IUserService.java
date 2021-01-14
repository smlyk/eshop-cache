package com.smlyk.eshopcache.service;

import com.smlyk.eshopcache.model.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author always
 * @since 2021-01-04
 */
public interface IUserService extends IService<User> {

    int updateUser(User user);

    User getUser(Integer id);

}
