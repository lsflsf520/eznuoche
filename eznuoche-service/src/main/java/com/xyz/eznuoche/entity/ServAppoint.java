package com.xyz.eznuoche.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.xyz.tools.common.constant.CheckState;
import com.xyz.tools.common.utils.DateUtil;
import com.xyz.tools.common.utils.EncryptTools;
import com.xyz.tools.common.utils.StringUtil;
import com.xyz.tools.db.bean.BaseEntity;

public class ServAppoint extends BaseEntity<Integer> {
    private Integer id;

    private Integer uid;

    private String kehuName;

    private String phone;

    private Date appointTime;

    private String servContent;

    private CheckState state;

    private Date createTime;

    private Date lastUptime;

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

    public String getKehuName() {
        return kehuName;
    }

    public void setKehuName(String kehuName) {
        this.kehuName = kehuName == null ? null : kehuName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public Date getAppointTime() {
        return appointTime;
    }

    public void setAppointTime(Date appointTime) {
        this.appointTime = appointTime;
    }

    public String getServContent() {
        return servContent;
    }

    public void setServContent(String servContent) {
        this.servContent = servContent == null ? null : servContent.trim();
    }

    public CheckState getState() {
        return state;
    }

    public void setState(CheckState state) {
        this.state = state;
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
        return id;
    }
    
    public String getStateName() {
    	if(CheckState.Deleted.equals(this.getState())) {
    		return "已删除";
    	}else if(CheckState.Closed.equals(this.state)) {
    		return "已取消";
    	}else if(CheckState.Passed.equals(this.state)) {
    		return "已成交";
    	}else if(CheckState.Checking.equals(this.state)) {
    		if(this.getAppointTime() != null && this.appointTime.getTime() > System.currentTimeMillis()) {
    			return "预约中";
    		}
    		return "已成交";
    	}
    	
    	return "状态异常";
    }
    
    public String getHidePhone() {
    	return StringUtils.isBlank(this.getPhone()) ? null : StringUtil.stringHide(getDecryptPhone());
    }
    
    public String getDecryptPhone() {
    	return EncryptTools.phoneDecrypt(this.getPhone());
    }
    
    public String getAppointTimeStr() {
    	return DateUtil.formatDate(this.appointTime, "MM-dd HH:mm");
    }
    
    /**
     * 
     * @return 如果距离预约时间小于1个小时了，则表明需要紧急联系客户了
     */
    public boolean isEmergent() {
    	return CheckState.Checking.equals(this.state) 
    			&& this.getAppointTime() != null 
    			&& this.appointTime.getTime() > System.currentTimeMillis()
    			&& this.appointTime.getTime() < System.currentTimeMillis() + 60l * 60 * 1000;
    }
    
}