package com.xyz.eznuoche.entity;

import java.util.Date;

import com.xyz.eznuoche.constant.MsgStatus;
import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.db.bean.BaseEntity;

public class NotifyLog extends BaseEntity<Integer> {
    private Integer id;

    private Integer uid;

    private Integer targetUid;

    private MsgType msgType;

    private String failReason;

    private MsgStatus notifyState;

    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(Integer targetUid) {
        this.targetUid = targetUid;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason == null ? null : failReason.trim();
    }

    public MsgStatus getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(MsgStatus notifyState) {
        this.notifyState = notifyState;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Integer getPK() {
        return id;
    }
}