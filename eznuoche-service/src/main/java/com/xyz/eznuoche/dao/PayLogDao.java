package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.PayLog;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface PayLogDao extends IBaseDao<Integer, PayLog> {
    Integer insertReturnPK(PayLog payLog);
}