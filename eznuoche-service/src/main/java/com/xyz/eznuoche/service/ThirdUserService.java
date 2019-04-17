package com.xyz.eznuoche.service;

import com.xyz.eznuoche.dao.ThirdUserDao;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class ThirdUserService extends AbstractBaseService<Integer, ThirdUser> {
    @Resource
    private ThirdUserDao thirdUserDao;

    @Override
    protected IBaseDao<Integer, ThirdUser> getBaseDao() {
        return thirdUserDao;
    }

    public Integer insertReturnPK(ThirdUser thirdUser) {
        thirdUserDao.insertReturnPK(thirdUser);
        return thirdUser.getPK();
    }

    public Integer doSave(ThirdUser thirdUser) {
        if (thirdUser.getPK() == null) {
            return this.insertReturnPK(thirdUser);
        }
        this.update(thirdUser);
        return thirdUser.getPK();
    }
}