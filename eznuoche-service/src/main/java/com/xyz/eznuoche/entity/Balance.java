package com.xyz.eznuoche.entity;

import com.xyz.tools.db.bean.BaseEntity;
import java.util.Date;

public class Balance extends BaseEntity<Integer> {
    private Integer uid;

    private Integer wxCnt;

    private Integer smsCnt;

    private Integer telCnt;

    private Date createTime;

    private Date lastUptime;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getWxCnt() {
        return wxCnt;
    }

    public void setWxCnt(Integer wxCnt) {
        this.wxCnt = wxCnt;
    }

    public Integer getSmsCnt() {
        return smsCnt;
    }

    public void setSmsCnt(Integer smsCnt) {
        this.smsCnt = smsCnt;
    }

    public Integer getTelCnt() {
        return telCnt;
    }

    public void setTelCnt(Integer telCnt) {
        this.telCnt = telCnt;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUptime() {
        return lastUptime;
    }

    public void setLastUptime(Date lastUptime) {
        this.lastUptime = lastUptime;
    }

    @Override
    public Integer getPK() {
        return uid;
    }
}