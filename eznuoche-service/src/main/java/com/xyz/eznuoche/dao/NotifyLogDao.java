package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.NotifyLog;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface NotifyLogDao extends IBaseDao<Integer, NotifyLog> {
    Integer insertReturnPK(NotifyLog notifyLog);
}