package com.gan.controller;

import com.gan.error.BizException;
import com.gan.error.EmBizError;
import com.gan.response.CommonReturnType;
import com.gan.service.OrderService;
import com.gan.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
/**
 * 订单Controller
 */
@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class OrderController extends BaseController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private HttpServletRequest httpServletRequest;

    /**
     *下单接口
     * @param itemId 商品id
     * @param promoId 秒杀id
     * @param amount 购买数量
     * @return 返回给前端的下单成功信息
     * @throws BizException
     */
    @RequestMapping(value = "/createorder",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "promoId",required = false)Integer promoId,
                                        @RequestParam(name = "amount")Integer amount) throws BizException {
        Boolean isLogin=(Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin==null||!isLogin.booleanValue())
            throw new BizException(EmBizError.USER_NOT_LOGIN,"用户还未登录，不能下单");
        //获取用户的登录信息
        UserModel userModel=(UserModel)httpServletRequest.getSession().getAttribute("LOGIN_USER");
        orderService.createOrder(userModel.getId(),itemId,promoId,amount);
        return CommonReturnType.create(null);
    }
}
