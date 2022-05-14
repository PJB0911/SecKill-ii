package com.gan.controller;

import com.alibaba.druid.util.StringUtils;
import com.gan.controller.viewobject.UserVO;
import com.gan.error.BizException;
import com.gan.error.EmBizError;
import com.gan.response.CommonReturnType;
import com.gan.service.UserService;
import com.gan.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户Controller
 */
@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
//    @Autowired
//    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 用户注册接口
     *
     * @param telphone 手机号码
     * @param otpCode  短信验证码
     * @param name     姓名
     * @param gender   性别
     * @param age      年龄
     * @param password 密码
     * @return 注册成功信息
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode") String otpCode,
                                     @RequestParam(name = "name") String name,
                                     @RequestParam(name = "gender") Integer gender,
                                     @RequestParam(name = "age") Integer age,
                                     @RequestParam(name = "password") String password) throws BizException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //验证手机号和对应的otpCode相符合
        //String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
        //改用redis
        String inSessionOtpCode=(String) redisTemplate.opsForValue().get(telphone);
        //工具类的equals已经进行了判空的处理
        if (!StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR, "短信验证码不符合！");
        }
        //用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setGender(new Byte(String.valueOf(gender.intValue())));
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setRegisterMode("byphone");
        userModel.setEncrptPassword(this.EncodeByMD5(password));//密码加密
        userService.register(userModel);
        return CommonReturnType.create(null);
    }

    /**
     * 用户登录接口
     *
     * @param telphone 手机号码
     * @param password 密码
     * @return 用户登录成功信息
     */
    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BizException, UnsupportedEncodingException, NoSuchAlgorithmException {
        //入参校验
        if (org.apache.commons.lang3.StringUtils.isEmpty(telphone) || org.apache.commons.lang3.StringUtils.isEmpty(password))
            throw new BizException(EmBizError.PARAMETER_VALIDATION_ERROR);
        //用户登录服务,用来校验用户登陆是否合法
        UserModel userModel = userService.validateLogin(telphone, this.EncodeByMD5(password));
             /*
                没有任何异常，则加入到用户登录成功的session内。该session会发送到redis服务器
                this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
                this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
            */
        //改用Token方式，将登陆信息和登录凭证一起存入redis中
        //生成Token，UUID保证唯一性
        String uuidToken = UUID.randomUUID().toString();
        uuidToken = uuidToken.replace("-", "");
        //建立Token与用户登录态的联系
        redisTemplate.opsForValue().set(uuidToken, userModel);
        //设置超时时间
        redisTemplate.expire(uuidToken, 1, TimeUnit.HOURS);
        //下发Token
        return CommonReturnType.create(uuidToken);
    }

    /**
     * 用户获取otp短信接口
     *
     * @param telphone 手机号码
     * @return 验证码
     */
    @RequestMapping(value = "/getOtp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        //需要按照一定规则生成OPT验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String optCode = String.valueOf(randomInt);

        //将验证码与用户手机号进行关联，使用HttpSession储存
        //httpServletRequest.getSession().setAttribute(telphone, optCode);

        //改用redis储存验证码
        redisTemplate.opsForValue().set(telphone,optCode);
        //设置超时时间
        redisTemplate.expire(telphone, 15, TimeUnit.MINUTES);
        //将OPT验证码通过短信通道发送给用户，省略
        System.out.println("telphone=" + telphone + "& otpCode=" + optCode);
        return CommonReturnType.create(null);
    }

    /**
     * 获取用户接口
     *
     * @param id 用户id
     * @return 返回给前端的用户信息
     * @throws BizException 业务错误异常
     */
    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BizException {
        //调用service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);
        //若获取的对应用户信息不存在
        if (userModel == null) {
            throw new BizException(EmBizError.USER_NOT_EXIST);
        }
        //将核心领域模型的用户对象转化为可供UI使用的viewobject
        UserVO userVO = convertFromModel(userModel);
        //返回通用对象
        return CommonReturnType.create(userVO);
    }

    /**
     * 将 UserModel 对象转换成 UserVO
     *
     * @param userModel userModel
     * @return UserVO
     */
    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) return null;
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel, userVO); //将 userDO 的属性复制到 userModel
        return userVO;
    }


    /**
     * MD5加密密码
     *
     * @param password 用户密码
     * @return 加密后的密码
     */
    private String EncodeByMD5(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        return base64Encoder.encode(md5.digest(password.getBytes("utf-8")));
    }
}
