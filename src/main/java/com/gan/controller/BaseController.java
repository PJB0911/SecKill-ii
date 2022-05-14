package com.gan.controller;

/**
 * 业务异常处理的BaseController
 */
public class BaseController {
    public static final String CONTENT_TYPE_FORMED = "application/x-www-form-urlencoded";


    /**
     * 定义ExceptionHandler解决未被Controller层吸收的异常
     * @param request  HttpServletRequest
     * @param ex   Exception
     * @return 错误信息
     */
 /*   @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex) {
        Map<String, Object> responseData = new HashMap<>();
        if (ex instanceof BizException) {
            BizException bizException = (BizException) ex;
            responseData.put("errCode", bizException.getErrCode());
            responseData.put("errMsg", bizException.getErrMsg());
            //打印堆栈信息，开发过程需要。发布后不需要
            ex.printStackTrace();
        } else {
            responseData.put("errCode", EmBizError.UNKNOWN_ERROR.getErrCode());
            responseData.put("errMsg", EmBizError.UNKNOWN_ERROR.getErrMsg());
            ex.printStackTrace();
        }
        return CommonReturnType.create(responseData, "fail");
    }*/
}
