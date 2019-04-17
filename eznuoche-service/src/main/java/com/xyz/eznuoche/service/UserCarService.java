package com.xyz.eznuoche.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xyz.eznuoche.dao.UserCarDao;
import com.xyz.eznuoche.entity.UserCar;
import com.xyz.tools.common.constant.CommonStatus;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class UserCarService extends AbstractBaseService<Integer, UserCar> {
    @Resource
    private UserCarDao userCarDao;

    @Override
    protected IBaseDao<Integer, UserCar> getBaseDao() {
        return userCarDao;
    }

    public Integer insertReturnPK(UserCar userCar) {
        userCarDao.insertReturnPK(userCar);
        return userCar.getPK();
    }

    public Integer doSave(UserCar userCar) {
        if (userCar.getPK() == null) {
            return this.insertReturnPK(userCar);
        }
        this.update(userCar);
        return userCar.getPK();
    }
    
    public List<UserCar> loadMyCars(int uid) {
    	UserCar query = new UserCar();
    	query.setUid(uid);
    	query.setState(CommonStatus.Normal);
    	
    	return this.findByEntity(query, "id.desc");
    }
}