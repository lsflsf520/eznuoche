package com.xyz.eznuoche.service;

import com.xyz.eznuoche.dao.RegUserDao;
import com.xyz.eznuoche.entity.RegUser;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RegUserService extends AbstractBaseService<Integer, RegUser> {
    @Resource
    private RegUserDao regUserDao;

    @Override
    protected IBaseDao<Integer, RegUser> getBaseDao() {
        return regUserDao;
    }

    public Integer insertReturnPK(RegUser regUser) {
        regUserDao.insertReturnPK(regUser);
        return regUser.getPK();
    }

    public Integer doSave(RegUser regUser) {
        if (regUser.getPK() == null) {
            return this.insertReturnPK(regUser);
        }
        this.update(regUser);
        return regUser.getPK();
    }
}