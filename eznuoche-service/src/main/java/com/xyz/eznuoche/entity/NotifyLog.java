package com.xyz.eznuoche.entity;

import com.xyz.tools.common.constant.MsgType;
import com.xyz.tools.db.bean.BaseEntity;
import java.util.Date;

public class NotifyLog extends BaseEntity<Integer> {
    private Integer id;

    private Integer uid;

    private Integer targetUid;

    private MsgType msgType;

    private String failReason;

    private String notifyState;

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

    public String getNotifyState() {
        return notifyState;
    }

    public void setNotifyState(String notifyState) {
        this.notifyState = notifyState == null ? null : notifyState.trim();
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