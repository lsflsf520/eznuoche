package com.xyz.eznuoche.dao;

import org.springframework.stereotype.Repository;

import com.xyz.eznuoche.entity.OfflineUserCar;
import com.xyz.tools.db.dao.IBaseDao;

@Repository
public interface OfflineUserCarDao extends IBaseDao<Integer, OfflineUserCar> {
    Integer insertReturnPK(OfflineUserCar offlineUserCar);
    
    /**
     * 
     * @return 返回当前有多少数据
     */
    int loadNum();
}