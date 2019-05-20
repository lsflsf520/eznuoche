package com.xyz.eznuoche.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.xyz.eznuoche.dao.ServAppointDao;
import com.xyz.eznuoche.entity.ServAppoint;
import com.xyz.tools.common.constant.CheckState;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.db.bean.PageData;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class ServAppointService extends AbstractBaseService<Integer, ServAppoint> {
    @Resource
    private ServAppointDao servAppointDao;

    @Override
    protected IBaseDao<Integer, ServAppoint> getBaseDao() {
        return servAppointDao;
    }

    public Integer insertReturnPK(ServAppoint servAppoint) {
    	servAppoint.setState(CheckState.Checking);
    	servAppoint.setCreateTime(new Date());
    	servAppoint.setLastUptime(servAppoint.getCreateTime());
        servAppointDao.insertReturnPK(servAppoint);
        return servAppoint.getPK();
    }
    
    @Override
    public boolean update(ServAppoint t) {
    	t.setLastUptime(new Date());
    	return super.update(t);
    }

    public Integer doSave(ServAppoint servAppoint) {
        if (servAppoint.getPK() == null) {
            return this.insertReturnPK(servAppoint);
        }
        this.update(servAppoint);
        return servAppoint.getPK();
    }
    
    public PageData<ServAppoint> loadMyServAppoints(int uid, int currPage) {
    	ServAppoint query = new ServAppoint();
    	query.setUid(uid);
    	
    	PageData<ServAppoint> dataPage = this.findByPage(query, currPage, BaseConfig.getInt("list.page.maxsize", 10), "state.desc,id.desc");
        if(!CollectionUtils.isEmpty(dataPage.getDatas())){
        	for(ServAppoint dbData : dataPage.getDatas()) {
        		if(CheckState.Checking.equals(dbData.getState()) && dbData.getAppointTime() != null && dbData.getAppointTime().getTime() < System.currentTimeMillis()) {
        			dbData.setState(CheckState.Passed);
        			
        			this.update(dbData);
        		}
        	}
        }
     
    	return dataPage;
    }
}