package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.UserCar;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCarDao extends IBaseDao<Integer, UserCar> {
    Integer insertReturnPK(UserCar userCar);
}