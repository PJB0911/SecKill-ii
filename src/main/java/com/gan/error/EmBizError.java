package com.gan.error;

/**
 * EmBusinessError 错误状态枚举类
 */
public enum EmBizError implements CommonError {
    ///10000开头为通用错误定义
    PARAMETER_VALIDATION_ERROR(100001, "参数不合法"),
    UNKNOWN_ERROR(100002, "未知错误"),

    ///20000开头为用户信息相关错误定义
    USER_NOT_EXIST(20001, "用户不存在"),
    USER_LOGIN_FAIL(20002, "用户手机或密码不正确"),
    USER_NOT_LOGIN(20003, "用户还未登录"),

    //30000开头为交易信息错误定义
    STOCK_NOT_ENOUGH(30001, "库存不足");

    /**
     * 错误码
     */
    private int errCode;
    /**
     * 错误描述
     */
    private String errMsg;

    private EmBizError(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }
}
