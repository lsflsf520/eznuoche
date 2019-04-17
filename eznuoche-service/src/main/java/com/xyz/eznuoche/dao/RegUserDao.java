package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.RegUser;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface RegUserDao extends IBaseDao<Integer, RegUser> {
    Integer insertReturnPK(RegUser regUser);
}