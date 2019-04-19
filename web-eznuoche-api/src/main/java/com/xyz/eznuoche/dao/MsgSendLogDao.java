package com.xyz.eznuoche.dao;

import org.springframework.stereotype.Repository;

import com.xyz.eznuoche.entity.MsgSendLog;
import com.xyz.tools.db.dao.IBaseDao;

@Repository
public interface MsgSendLogDao extends IBaseDao<Integer, MsgSendLog> {
    Integer insertReturnPK(MsgSendLog msgSendLog);
}