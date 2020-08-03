package com.gan.dao;


import com.gan.dataobject.SequenceDO;
/**
 * Sequence序列Mapper
 */
public interface SequenceDOMapper {
    //过名字获取序列信息，要上锁
    SequenceDO getSequenceByName(String name);

    int deleteByPrimaryKey(String name);

    int insert(SequenceDO record);

    int insertSelective(SequenceDO record);

    SequenceDO selectByPrimaryKey(String name);

    int updateByPrimaryKeySelective(SequenceDO record);

    int updateByPrimaryKey(SequenceDO record);
}