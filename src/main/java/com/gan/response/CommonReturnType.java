package com.gan.response;

/**
 * 通用返回结果类型
 */
public class CommonReturnType {
    /**
     * 对应请求的返回处理结果 "success" 或 "fail"
     */
    private String status;
    /**
     * 返回的数据：
     * 若success，data返回前端需要的JSON数据；
     * 若fail，则data使用通用的错误码格式
     */
    private Object data;

    /**
     * 定义一个通用的创建方法（success）
     *
     * @param result 传入的数据
     * @return 通用返回结果类型
     */
    public static CommonReturnType create(Object result) {
        return CommonReturnType.create(result, "success");
    }

    /**
     * 定义一个通用的创建方法（自定义）
     *
     * @param result 传入的数据
     * @param status 处理结果 success or fail
     * @return 通用返回结果类型
     */
    public static CommonReturnType create(Object result, String status) {
        CommonReturnType type = new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
