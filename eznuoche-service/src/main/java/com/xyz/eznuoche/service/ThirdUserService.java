package com.xyz.eznuoche.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xyz.eznuoche.dao.ThirdUserDao;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

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
    
    /**
     * 第三方用户的唯一标识，比如微信的openId
     * @param channelUid
     * @return
     */
    public ThirdUser loadByChannelUid(String channelUid) {
    	ThirdUser query = new ThirdUser();
    	query.setThirdUid(channelUid);
    	
    	return this.findOne(query);
    }
    
    
    public ThirdUser loadByUid(int uid) {
    	ThirdUser query = new ThirdUser();
    	query.setUid(uid);
    	
    	return this.findOne(query);
    }
}