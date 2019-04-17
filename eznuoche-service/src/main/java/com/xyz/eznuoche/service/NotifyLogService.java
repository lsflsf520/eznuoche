package com.xyz.eznuoche.service;

import com.xyz.eznuoche.dao.NotifyLogDao;
import com.xyz.eznuoche.entity.NotifyLog;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class NotifyLogService extends AbstractBaseService<Integer, NotifyLog> {
    @Resource
    private NotifyLogDao notifyLogDao;

    @Override
    protected IBaseDao<Integer, NotifyLog> getBaseDao() {
        return notifyLogDao;
    }

    public Integer insertReturnPK(NotifyLog notifyLog) {
        notifyLogDao.insertReturnPK(notifyLog);
        return notifyLog.getPK();
    }

    public Integer doSave(NotifyLog notifyLog) {
        if (notifyLog.getPK() == null) {
            return this.insertReturnPK(notifyLog);
        }
        this.update(notifyLog);
        return notifyLog.getPK();
    }
}