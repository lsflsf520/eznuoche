package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.Balance;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceDao extends IBaseDao<Integer, Balance> {
    Integer insertReturnPK(Balance balance);
}