package com.xyz.eznuoche.dao;

import com.xyz.eznuoche.entity.ServAppoint;
import com.xyz.tools.db.dao.IBaseDao;
import org.springframework.stereotype.Repository;

@Repository
public interface ServAppointDao extends IBaseDao<Integer, ServAppoint> {
    Integer insertReturnPK(ServAppoint servAppoint);
}