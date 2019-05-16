package com.xyz.eznuoche.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.github.miemiedev.mybatis.paginator.domain.Order.Direction;
import com.xyz.eznuoche.dao.UserCarDao;
import com.xyz.eznuoche.entity.RegUser;
import com.xyz.eznuoche.entity.UserCar;
import com.xyz.tools.common.constant.CommonStatus;
import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class UserCarService extends AbstractBaseService<Integer, UserCar> {
    @Resource
    private UserCarDao userCarDao;
    @Resource
    private RegUserService regUserService;

    @Override
    protected IBaseDao<Integer, UserCar> getBaseDao() {
        return userCarDao;
    }

    public Integer insertReturnPK(UserCar userCar) {
    	userCar.setState(CommonStatus.Normal);
    	userCar.setCreateTime(new Date());
    	userCar.setLastUptime(new Date());
        userCarDao.insertReturnPK(userCar);
        return userCar.getPK();
    }
    
    @Override
    public boolean update(UserCar t) {
    	t.setLastUptime(new Date());
    	return super.update(t);
    }
    
    @Override
    protected String getStatusFieldName() {
    	return "state";
    }
    
    public Integer doSave(UserCar userCar) {
        if (userCar.getPK() == null) {
        	if(StringUtils.isBlank(userCar.getPlateNo())){
        		throw new BaseRuntimeException("NULL_PLATE_NO", "车牌号不能为空");
        	}
        	
        	UserCar dbData = loadByPlateNo(userCar.getPlateNo());
        	if(dbData != null) {
        		RegUser regUser = regUserService.findById(dbData.getUid());
        		throw new BaseRuntimeException("ALREADY_EXIST", "当前车牌号已被手机号为" + regUser.getHidePhone() + "的用户绑定");
        	}
        	
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
    
    public Integer saveCarNum(int uid, String plateNo) {
    	if(StringUtils.isBlank(plateNo) || plateNo.length() <= 1) {
    		LogUtils.warn("plateNo cannot be null to save");
    		return 0;
    	}
    	
    	UserCar updata = new UserCar();
    	updata.setUid(uid);
    	updata.setPlateNo(plateNo.toUpperCase());
    	
    	return this.doSave(updata);
    }
    
    public UserCar loadByPlateNo(String plateNo) {
    	UserCar query = new UserCar();
    	query.setPlateNo(plateNo);
    	query.setState(CommonStatus.Normal);
    	
    	return this.findOne(query, "id", Direction.DESC);
    }
}