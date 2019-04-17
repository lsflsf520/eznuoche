package com.xyz.eznuoche.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xyz.eznuoche.dao.BalanceDao;
import com.xyz.eznuoche.entity.Balance;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class BalanceService extends AbstractBaseService<Integer, Balance> {
    @Resource
    private BalanceDao balanceDao;

    @Override
    protected IBaseDao<Integer, Balance> getBaseDao() {
        return balanceDao;
    }

    /*public Integer insertReturnPK(Balance balance) {
        balanceDao.insertReturnPK(balance);
        return balance.getPK();
    }

    public Integer doSave(Balance balance) {
        if (balance.getPK() == null) {
            return this.insertReturnPK(balance);
        }
        this.update(balance);
        return balance.getPK();
    }*/
    
    public void init(int uid) {
    	if(this.findById(uid) != null) {
    		LogUtils.warn("balance has init for uid %d，then ignore it", uid);
    		return;
    	}
    	Balance updata = new Balance();
    	updata.setUid(uid);
    	updata.setWxCnt(BaseConfig.getInt("balance.wx.init.num", Integer.MAX_VALUE));
    	updata.setSmsCnt(BaseConfig.getInt("balance.sms.init.num", 3));
    	updata.setTelCnt(BaseConfig.getInt("balance.tel.init.num", 3));
    	updata.setCreateTime(new Date());
    	updata.setLastUptime(updata.getCreateTime());
    	
    	this.insert(updata);
    }
    
}