package com.xyz.eznuoche.service;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xyz.eznuoche.constant.MsgStatus;
import com.xyz.eznuoche.dao.BalanceDao;
import com.xyz.eznuoche.entity.Balance;
import com.xyz.eznuoche.entity.NotifyLog;
import com.xyz.eznuoche.entity.PayLog;
import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class BalanceService extends AbstractBaseService<Integer, Balance> implements IBalanceService{
    @Resource
    private BalanceDao balanceDao;
    
    @Resource
    private NotifyLogService notifyLogService;
    
    @Resource
    private PayLogService payLogService;

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
    
    @Transactional(value="eznuocheTransactionManager")
    public void minusBalance(int targetUid, MsgType msgType, int uid) {
    	String fieldName = null;
    	if(MsgType.WX.equals(msgType)){
    		fieldName = "wx_cnt";
    	} else if(MsgType.SMS.equals(msgType)) {
    		fieldName = "sms_cnt";
    	} else if(MsgType.TEL.equals(msgType)) {
    		fieldName = "tel_cnt";
    	}
    	if(StringUtils.isBlank(fieldName)){
    		throw new BaseRuntimeException("NOT_SUPPORT", "不支持的类型");
    	}
    	balanceDao.minusBalance(fieldName, uid);
    	
    	NotifyLog updata = new NotifyLog();
    	updata.setMsgType(msgType);
    	updata.setUid(uid);
    	updata.setTargetUid(targetUid);
    	updata.setNotifyState(MsgType.TEL.equals(msgType) ? MsgStatus.CONFIRM : MsgStatus.SUCCESS);
    	updata.setCreateTime(new Date());
    	notifyLogService.insert(updata);
    }
    
    @Transactional(value="eznuocheTransactionManager")
    public void addBalance(PayLog payLog, String thirdTradeNo,  MsgType msgType, int num) {
    	String fieldName = null;
    	if(MsgType.WX.equals(msgType)){
    		fieldName = "wx_cnt";
    	} else if(MsgType.SMS.equals(msgType)) {
    		fieldName = "sms_cnt";
    	} else if(MsgType.TEL.equals(msgType)) {
    		fieldName = "tel_cnt";
    	}
    	if(StringUtils.isBlank(fieldName)){
    		throw new BaseRuntimeException("NOT_SUPPORT", "不支持的类型");
    	}
    	balanceDao.addBalance(num, fieldName, payLog.getUid());
    	
    	payLogService.paySuccess(payLog.getId(), thirdTradeNo);
    }
    
}