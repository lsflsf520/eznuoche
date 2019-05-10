package com.xyz.eznuoche.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.xyz.eznuoche.entity.Balance;
import com.xyz.tools.db.dao.IBaseDao;

@Repository
public interface BalanceDao extends IBaseDao<Integer, Balance> {
    Integer insertReturnPK(Balance balance);
    
    /**
     * 
     * @param fieldName 字段名
     * @param uid
     * @return 
     */
    int minusBalance(@Param("fieldName") String fieldName, @Param("uid") int uid);
    
    /**
     * 
     * @param num 添加通知的次数
     * @param fieldName 字段名
     * @param uid
     * @return
     */
    int addBalance(@Param("num") int num, @Param("fieldName") String fieldName, @Param("uid") int uid);
    
    /**
     * 
     * @param num
     * @param uid
     * @return
     */
    int addBalance4Inviter(@Param("smsNum") int smsNum, @Param("telNum") int telNum, @Param("uid") int uid);
}