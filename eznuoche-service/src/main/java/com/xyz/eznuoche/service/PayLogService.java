package com.xyz.eznuoche.service;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xyz.eznuoche.dao.PayLogDao;
import com.xyz.eznuoche.entity.PayLog;
import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.common.constant.OrdState;
import com.xyz.tools.common.utils.RandomUtil;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class PayLogService extends AbstractBaseService<Integer, PayLog> {
    @Resource
    private PayLogDao payLogDao;
    

    @Override
    protected IBaseDao<Integer, PayLog> getBaseDao() {
        return payLogDao;
    }

    public Integer insertReturnPK(PayLog payLog) {
        payLogDao.insertReturnPK(payLog);
        return payLog.getPK();
    }

    public Integer doSave(PayLog payLog) {
        if (payLog.getPK() == null) {
            return this.insertReturnPK(payLog);
        }
        this.update(payLog);
        return payLog.getPK();
    }
    
    public boolean paySuccess(int payLogId, String thirdTradeNo) {
    	return this.updateState(payLogId, OrdState.Payed, thirdTradeNo);
    }
    
    private boolean updateState(int payLogId, OrdState ordState, String thirdTradeNo) {
    	PayLog updata = new PayLog();
    	updata.setId(payLogId);
    	updata.setState(ordState);
    	updata.setThirdTradeNo(thirdTradeNo);
    	updata.setLastUptime(new Date());
    	
    	return this.update(updata);
    }
    
    /**
     * 生成对外交易号
     * @return
     */
    public String genOutTradeNo() {
    	return System.currentTimeMillis() + "" + RandomUtil.randomAlphaNumCode(3);
    }
    
    public int genPayLog(int uid, MsgType msgType, String outTradeNo, int amount) {
    	PayLog updata = new PayLog();
    	updata.setUid(uid);
    	updata.setMsgType(msgType);
    	updata.setAmount(amount);
    	updata.setOutTradeNo(outTradeNo);
    	updata.setState(OrdState.Paying);
    	updata.setCreateTime(new Date());
    	updata.setLastUptime(updata.getCreateTime());
    	
    	return this.insertReturnPK(updata);
    }
    
    public PayLog loadByOutTradeNo(String outTradeNo) {
    	PayLog query = new PayLog();
    	query.setOutTradeNo(outTradeNo);
    	
    	return this.findOne(query);
    }
}