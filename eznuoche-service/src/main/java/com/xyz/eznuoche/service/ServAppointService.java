package com.xyz.eznuoche.service;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.github.miemiedev.mybatis.paginator.domain.Order;
import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.github.miemiedev.mybatis.paginator.domain.PageList;
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
    
    public PageData<ServAppoint> loadMyIncomingServAppoints(int uid, int currPage){
    	PageData<ServAppoint> dataPage = this.loadServAppointByPage(uid, currPage, "appoint_time.asc", CheckState.Checking);
    	if(!CollectionUtils.isEmpty(dataPage.getDatas())){
        	for(ServAppoint dbData : dataPage.getDatas()) {
        		if(dbData.getAppointTime() != null && dbData.getAppointTime().getTime() < System.currentTimeMillis()) {
        			dbData.setState(CheckState.Passed);
        			
        			this.update(dbData);
        		}
        	}
        }
     
    	return dataPage;
    }
    
    public PageData<ServAppoint> loadMyHistoryServAppoints(int uid, int currPage){
    	PageData<ServAppoint> dataPage = this.loadServAppointByPage(uid, currPage, "appoint_time.desc", CheckState.Passed, CheckState.Closed);
    	
    	return dataPage;
    }
    
    private PageData<ServAppoint> loadServAppointByPage(int uid, int currPage, String ordseg, CheckState... checkStates){
    	PageBounds pageBounds = new PageBounds(currPage, BaseConfig.getInt("list.page.maxsize", 10));
    	if(StringUtils.isNotBlank(ordseg)) {
    		List<Order> orders = Order.formString(ordseg);
    		pageBounds.setOrders(orders);
    	}
		
		List<ServAppoint> tlist = servAppointDao.loadMyAppoints(uid, pageBounds, checkStates);
		
		return new PageData<ServAppoint>((PageList<ServAppoint>)tlist);
	}
}