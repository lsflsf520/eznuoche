package com.xyz.eznuoche.service;

import com.xyz.eznuoche.dao.PayLogDao;
import com.xyz.eznuoche.entity.PayLog;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

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
}