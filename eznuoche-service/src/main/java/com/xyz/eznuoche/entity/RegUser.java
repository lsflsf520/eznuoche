package com.xyz.eznuoche.entity;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xyz.tools.common.constant.CommonStatus;
import com.xyz.tools.common.constant.Sex;
import com.xyz.tools.common.utils.EncryptTools;
import com.xyz.tools.common.utils.StringUtil;
import com.xyz.tools.db.bean.BaseEntity;
import com.xyz.tools.web.util.WebUtils;

public class RegUser extends BaseEntity<Integer> {
    private Integer id;

    private String nickName;

    private String phone;

    private String passwd;

    private Sex sex;

    private String headImg;
    
    private String myCode;

    private String inviteUid;
    
    private String regIp;

    private CommonStatus state;

    private Date createTime;

    private Date lastUptime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName == null ? null : nickName.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg == null ? null : headImg.trim();
    }
    
    public String getMyCode() {
        return myCode;
    }

    public void setMyCode(String myCode) {
        this.myCode = myCode == null ? null : myCode.trim();
    }


    public String getInviteUid() {
        return inviteUid;
    }

    public void setInviteUid(String inviteUid) {
        this.inviteUid = inviteUid == null ? null : inviteUid.trim();
    }
    
    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp == null ? null : regIp.trim();
    }

    public CommonStatus getState() {
        return state;
    }

    public void setState(CommonStatus state) {
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
    
    public boolean isNormal() {
    	return CommonStatus.Normal.equals(this.getState());
    }
    
    public String getHidePhone() {
    	return StringUtils.isBlank(this.getPhone()) ? null : StringUtil.stringHide(getDecryptPhone());
    }
    
    @JsonIgnore
    public String getDecryptPhone() {
    	return EncryptTools.phoneDecrypt(this.getPhone());
    }
    
    public String getWellHeadImg() {
    	return WebUtils.wellformUrl(StringUtils.isBlank(headImg) ? "/manual/defhead.png" : headImg);
    }

	
	public String getShowName() {
		return StringUtils.isBlank(this.getNickName()) ? this.getHidePhone() : this.getNickName();
	}
	
	public String getMyRecLinkCode() {
		return StringUtils.isBlank(this.getInviteUid()) || "0".equals(this.getInviteUid()) ? this.getId() + "" : this.getInviteUid() + "," + this.getId();
	}
}