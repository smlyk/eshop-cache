package com.smlyk.eshopcache.controller;


import com.smlyk.eshopcache.dto.Result;
import com.smlyk.eshopcache.model.User;
import com.smlyk.eshopcache.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author always
 * @since 2021-01-04
 */
@RestController
@RequestMapping("/user")
public class UserController {

    public static final String CACHE_NAME = "test_cache";

    @Autowired
    private IUserService userService;

    @GetMapping("getUser")
    //@Cacheable可以标记在一个方法上，也可以标记在一个类上。当标记在一个方法上时表示该方法是支持缓存的，当标记在一个类上时则表示该类所有的方法都是支持缓存的。
    @Cacheable(value = CACHE_NAME, key = "'user_' + #id" )
    public Result getUser(@RequestParam Integer id){
        return new Result().success(userService.getUser(id));
    }

    @GetMapping("updateUser")
    //@CacheEvict是用来标注在需要清除缓存元素的方法或类上的。
    @CacheEvict(value = CACHE_NAME, key = "'user_' + #id" )
    public Result updateUser(@RequestParam Integer id, @RequestParam(required = false) String name, @RequestParam String address){
        return new Result().success(userService.updateUser(new User(id, name, address)));
    }

}
