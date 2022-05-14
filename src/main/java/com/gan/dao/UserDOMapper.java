package com.gan.dao;


import com.gan.dataobject.UserDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户Mapper
 */
public interface UserDOMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserDO record);

    int insertSelective(UserDO record);

    int updateByPrimaryKeySelective(UserDO record);

    int updateByPrimaryKey(UserDO record);

    UserDO selectByPrimaryKey(Integer id);

    UserDO selectByTelphone(String telphone);
}