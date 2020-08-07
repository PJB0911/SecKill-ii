package com.gan.service.impl;


import com.gan.dao.UserDOMapper;
import com.gan.dao.UserPasswordDOMapper;
import com.gan.dataobject.UserDO;
import com.gan.dataobject.UserPasswordDO;
import com.gan.error.BizException;
import com.gan.error.EmBizError;
import com.gan.service.UserService;
import com.gan.service.model.UserModel;
import com.gan.validator.ValidationResult;
import com.gan.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDOMapper userDOMapper;
    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;
    @Autowired
    private ValidatorImpl validator;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public UserModel getUserById(Integer id) {
        //调用userdomapper获取到对应的用户dataobject
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if (userDO == null)
            return null;
        //通过用户id获取对应的用户加密密码信息
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObj(userDO, userPasswordDO);
    }

    @Override
    public UserModel validateLogin(String telphone, String encrptPassword) throws BizException {
        //通过手机获取用户信息
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if (userDO == null) {
            throw new BizException(EmBizError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserModel userModel = convertFromDataObj(userDO, userPasswordDO);
        //比对用户信息的密码与传输进来的密码是否匹配 (MD5加密后的密码)
        if (!StringUtils.equals(encrptPassword, userModel.getEncrptPassword())) {
            throw new BizException(EmBizError.USER_LOGIN_FAIL);
        }
        return userModel;
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BizException {
        if (userModel == null) {
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR);
        }
        ValidationResult result = validator.validate(userModel);
        if (result.isHasErrors()) {
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        //Model转成DataObject
        UserDO userDO = convertFromModel(userModel);
        //这里使用insertSelective，不使用insert方法
        try {
            userDOMapper.insertSelective(userDO);
        } catch (DuplicateKeyException e) {
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "手机号已被注册");
        }
        //得到主键
        userModel.setId(userDO.getId());
        //处理Password
        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        userPasswordDOMapper.insertSelective(userPasswordDO);
    }


    @Override
    public UserModel getUserByIdInCache(Integer id) {
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get("user_validate_" + id);
        if (userModel == null) {
            userModel = this.getUserById(id);
            redisTemplate.opsForValue().set("user_validate_" + id, userModel);
            redisTemplate.expire("user_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return userModel;
    }



    /**
     * 将 UserDO 对象转换成 UserModel
     *
     * @param userModel UserModel
     * @return UserDO
     */
    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null)
            return null;
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO); //将 userModel 的属性复制到  userDO
        return userDO;
    }

    /**
     * 将 UserModel 对象转换成 userPasswordDO
     *
     * @param userModel UserModel
     * @return userPasswordDO
     */
    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null)
            return null;
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());
        return userPasswordDO;
    }

    /**
     * 将 UserDO 对象转换成UserModel
     *
     * @param userDO         UserDO
     * @param userPasswordDO UserPasswordDO
     * @return UserModel
     */
    private UserModel convertFromDataObj(UserDO userDO, UserPasswordDO userPasswordDO) {
        if (userDO == null)
            return null;
        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO, userModel); //将userDO的属性复制到userModel
        if (userPasswordDO != null) {
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userModel;
    }
}
