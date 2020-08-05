package com.gan.controller;

import com.gan.error.BizException;
import com.gan.error.EmBizError;
import com.gan.response.CommonReturnType;
import com.gan.service.OrderService;
import com.gan.service.model.UserModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 订单Controller
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 下单接口
     *
     * @param itemId  商品id
     * @param promoId 秒杀id
     * @param amount  购买数量
     * @return 返回给前端的下单成功信息
     * @throws BizException
     */
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "promoId", required = false) Integer promoId,
                                        @RequestParam(name = "amount") Integer amount) throws BizException {
        /*用sessionId的方式 验证用户信息
            Boolean isLogin=(Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
            if(isLogin==null||!isLogin.booleanValue())
                throw new BizException(EmBizError.USER_NOT_LOGIN,"用户还未登录，不能下单");
            UserModel userModel=(UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");*/

        //使用Token的方式
        String token = httpServletRequest.getParameterMap().get("token")[0];
        if (StringUtils.isEmpty(token)) {
            throw new BizException(EmBizError.USER_NOT_LOGIN, "用户还未登录，不能下单");
        }
        UserModel userModel = (UserModel) redisTemplate.opsForValue().get(token);
        if (userModel == null) {
            throw new BizException(EmBizError.USER_NOT_LOGIN, "登录过期，请重新登录");
        }
        orderService.createOrder(userModel.getId(), itemId, promoId, amount);
        return CommonReturnType.create(null);
    }
}
