package com.gan.error;

/**
 * 通用错误信息
 */
public interface CommonError {
    /**
     * 返回错误码
     * @return 错误码
     */
    int getErrCode();

    /**
     * 返回错误描述
     * @return 错误描述
     */
    String getErrMsg();

    /**
     *  设置错误描述
     * @param errMsg 错误描述
     * @return 通用错误信息CommonError
     */
    CommonError setErrMsg(String errMsg);
}
