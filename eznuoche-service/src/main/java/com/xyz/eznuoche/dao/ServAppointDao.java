package com.xyz.eznuoche.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.github.miemiedev.mybatis.paginator.domain.PageBounds;
import com.xyz.eznuoche.entity.ServAppoint;
import com.xyz.tools.common.constant.CheckState;
import com.xyz.tools.db.dao.IBaseDao;

@Repository
public interface ServAppointDao extends IBaseDao<Integer, ServAppoint> {
    Integer insertReturnPK(ServAppoint servAppoint);
    
    /**
     * 查询商户指定的预约记录
     * @param uid 
     * @param checkState 预约状态
     * @param pageBounds
     * @return
     */
    List<ServAppoint> loadMyAppoints(@Param("uid") int uid, PageBounds pageBounds, @Param("checkStates") CheckState... checkStates);
}