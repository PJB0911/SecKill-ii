package com.gan.service;


/**
 * 封装本地缓存操作类
 */
public interface CacheService {
    /**
     * 存缓存
     * @param key 键
     * @param value 值
     */
    void setCommonCache(String key, Object value);
    /**
     * 取缓存
     * @param key 键
     */
    Object getFromCommonCache(String key);
}
