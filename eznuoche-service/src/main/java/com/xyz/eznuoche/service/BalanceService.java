package com.xyz.eznuoche.service;

import com.xyz.eznuoche.dao.BalanceDao;
import com.xyz.eznuoche.entity.Balance;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class BalanceService extends AbstractBaseService<Integer, Balance> {
    @Resource
    private BalanceDao balanceDao;

    @Override
    protected IBaseDao<Integer, Balance> getBaseDao() {
        return balanceDao;
    }

    public Integer insertReturnPK(Balance balance) {
        balanceDao.insertReturnPK(balance);
        return balance.getPK();
    }

    public Integer doSave(Balance balance) {
        if (balance.getPK() == null) {
            return this.insertReturnPK(balance);
        }
        this.update(balance);
        return balance.getPK();
    }
}