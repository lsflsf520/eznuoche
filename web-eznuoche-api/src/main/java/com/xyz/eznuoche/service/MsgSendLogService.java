package com.xyz.eznuoche.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xyz.eznuoche.dao.MsgSendLogDao;
import com.xyz.eznuoche.entity.MsgSendLog;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;

@Service
public class MsgSendLogService extends AbstractBaseService<Integer, MsgSendLog> {
    @Resource
    private MsgSendLogDao msgSendLogDao;

    @Override
    protected IBaseDao<Integer, MsgSendLog> getBaseDao() {
        return msgSendLogDao;
    }

    public Integer insertReturnPK(MsgSendLog msgSendLog) {
        msgSendLogDao.insertReturnPK(msgSendLog);
        return msgSendLog.getPK();
    }

    public Integer doSave(MsgSendLog msgSendLog) {
        if (msgSendLog.getPK() == null) {
            return this.insertReturnPK(msgSendLog);
        }
        this.update(msgSendLog);
        return msgSendLog.getPK();
    }
    
}