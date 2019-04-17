package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface ThirdUserDao extends IBaseDao<Integer, ThirdUser> {
    Integer insertReturnPK(ThirdUser thirdUser);
}