package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.OfflineUserCar;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface OfflineUserCarDao extends IBaseDao<Integer, OfflineUserCar> {
    Integer insertReturnPK(OfflineUserCar offlineUserCar);
}