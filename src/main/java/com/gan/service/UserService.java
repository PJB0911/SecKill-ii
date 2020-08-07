package com.gan.service;

import com.gan.error.BizException;
import com.gan.service.model.UserModel;

/**
 * 用户Service
 */
public interface UserService {
    /**
     * 根据用户id查询信息
     * @param id 用户id
     * @return userModel对象
     */
    UserModel getUserById(Integer id);

    /**
     * 用户注册
     * @param userModel userModel对象
     * @throws BizException
     */
    void register(UserModel userModel) throws BizException;

    /**
     * 用户登录
     * @param telphone 手机号
     * @param encrptPassword 加密后的密码
     * @return  userModel对象
     * @throws BizException
     */

    UserModel validateLogin(String telphone, String encrptPassword) throws BizException;

    /**
     * 优化：通过缓存获取用户对象
     * @param id 用户id
     * @return userModel对象
     */
    UserModel getUserByIdInCache(Integer id);
}
