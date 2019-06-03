package com.xyz.eznuoche.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.Order.Direction;
import com.xyz.eznuoche.dao.OfflineUserCarDao;
import com.xyz.eznuoche.entity.OfflineUserCar;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class OfflineUserCarService extends AbstractBaseService<Integer, OfflineUserCar> {
    @Resource
    private OfflineUserCarDao offlineUserCarDao;

    @Override
    protected IBaseDao<Integer, OfflineUserCar> getBaseDao() {
        return offlineUserCarDao;
    }

    public Integer insertReturnPK(OfflineUserCar offlineUserCar) {
        offlineUserCarDao.insertReturnPK(offlineUserCar);
        return offlineUserCar.getPK();
    }

    public Integer doSave(OfflineUserCar offlineUserCar) {
        if (offlineUserCar.getPK() == null) {
            return this.insertReturnPK(offlineUserCar);
        }
        this.update(offlineUserCar);
        return offlineUserCar.getPK();
    }
    
    public OfflineUserCar loadByPlateNo(String plateNo) {
    	OfflineUserCar query = new OfflineUserCar();
    	query.setPlateNo(plateNo);
    	
    	return this.findOne(query, "id", Direction.DESC);
    }
    
    public int loadNum() {
    	return this.offlineUserCarDao.loadNum();
    }
}