package com.xyz.eznuoche.service;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.xyz.eznuoche.dao.RegUserDao;
import com.xyz.eznuoche.entity.RegUser;
import com.xyz.eznuoche.entity.ThirdUser;
import com.xyz.eznuoche.utils.EzConstant;
import com.xyz.tools.cache.redis.RedisDistLock;
import com.xyz.tools.common.constant.CommonStatus;
import com.xyz.tools.common.constant.GlobalConstant;
import com.xyz.tools.common.constant.Sex;
import com.xyz.tools.common.exception.BaseRuntimeException;
import com.xyz.tools.common.utils.BaseConfig;
import com.xyz.tools.common.utils.EncryptTools;
import com.xyz.tools.common.utils.LogUtils;
import com.xyz.tools.common.utils.RandomUtil;
import com.xyz.tools.common.utils.RegexUtil;
import com.xyz.tools.common.utils.StringUtil;
import com.xyz.tools.common.utils.ThreadUtil;
import com.xyz.tools.db.bean.PageData;
import com.xyz.tools.db.dao.IBaseDao;
import com.xyz.tools.db.service.AbstractBaseService;
import com.xyz.tools.web.util.LogonUtil;
import com.xyz.tools.web.util.LogonUtil.SessionUser;
import com.xyz.tools.web.util.LogonUtil.UserCls;

@Service
public class RegUserService extends AbstractBaseService<Integer, RegUser> {
    @Resource
    private RegUserDao regUserDao;
    @Resource
    private IBalanceService balanceService;

    @Override
    protected IBaseDao<Integer, RegUser> getBaseDao() {
        return regUserDao;
    }

    
    public Integer insertReturnPK(RegUser user) {
		user.setCreateTime(new Date());
		user.setLastUptime(new Date());
		user.setState(CommonStatus.Normal);
		regUserDao.insertReturnPK(user);
		return user.getPK();
	}
    
	public Integer doSave(RegUser user) {
    	user.setLastUptime(new Date());
		if (user.getPK() == null) {
//			if (!RegexUtil.isPhone(user.getPhone())) {
//				throw new BaseRuntimeException("ILLEGAL_PARAM", "手机号(" + user.getPhone() + ")格式不正确");
//			}
			RegUser dbData = loadByEncyptPhone(user.getPhone());
			if (dbData != null) {
				throw new BaseRuntimeException("ALREADY_EXIST", "手机号已存在");
			}
			user.setCreateTime(user.getLastUptime());
			return this.insertReturnPK(user);
		}
		// 密码重置必须通过专门的接口处理
		if (StringUtils.isNotBlank(user.getPasswd())) {
			user.setPasswd(null);
		}

		this.update(user);
		return user.getPK();
	}

	@Override
	public boolean update(RegUser t) {
		t.setPhone(null); // 修改个人信息的时候不能修改手机号
		t.setLastUptime(new Date());
		return super.update(t);
	}

	public RegUser loadByEncyptPhone(String phone) {
		RegUser query = new RegUser();
//		query.setPhone(BaseConstant.base64Encrypt(phone));
		query.setPhone(phone);
		RegUser dbData = this.findOne(query);
		
		return dbData;
	}
	
	private RegUser loadByInviteCode(String inviteCode){
		if(StringUtils.isBlank(inviteCode)){
			throw new BaseRuntimeException("ILLEGAL_PARAM", "邀请码不能为空");
		}
		RegUser query = new RegUser();
		query.setMyCode(inviteCode);
		query.setState(CommonStatus.Normal);
		
		return this.findOne(query);
	}
	
	public RegUser doReg4User(String phone, String password, int channelId, String inviteCode) {
		RegUser inviter = null;
		if(StringUtils.isNotBlank(inviteCode)) {
			if(RegexUtil.isPhone(inviteCode)) {
				inviter = this.loadByEncyptPhone(EncryptTools.phoneEncypt(inviteCode));
			} else {
				inviter = loadByInviteCode(inviteCode);
			}
			if(inviter == null) {
				LogUtils.warn("not found inviter with inviteCode %s", inviteCode);
			} 
		}
		String parentIdStr = buildInviteUid(inviter);//父节点字符串
		
		return doReg(phone, StringUtil.stringHide(phone), password, parentIdStr);
	}
	
	public RegUser doReg4ChannelUser(String phone, String nickName, String parentIdStr) {
		
		return doReg(phone, nickName, null, StringUtils.isNotBlank(parentIdStr) ? parentIdStr : EzConstant.Def_Invite_Uid);
	}
	
	public static String buildInviteUid(RegUser inviter) {
		String parentIdStr = EzConstant.Def_Invite_Uid;//父节点字符串
		if(inviter != null){
			parentIdStr = (StringUtils.isBlank(inviter.getInviteUid()) ? EzConstant.Def_Invite_Uid : inviter.getInviteUid() ) + "," + inviter.getId();
		}
		
		return parentIdStr;
	}
	
	/**
	 * 适用于管理端添加用户或代理人
	 * @param phone 手机号
	 * @param nickName 用户昵称
	 * @param password 明文密码
	 * @param parentIdStr 推荐人id链
	 * @return
	 */
	public RegUser doReg(String phone, String nickName, String password, String parentIdStr) {
		try{
			boolean locked = RedisDistLock.trylock("register", phone);
			if(locked) {
				RegUser updata = new RegUser();
				updata.setPhone(EncryptTools.phoneEncypt(phone));
				
				if(StringUtils.isNotBlank(password)) {
					String md5pass = encryptPass(password);
					updata.setPasswd(md5pass);
				}
				updata.setInviteUid(StringUtils.isBlank(parentIdStr) ? EzConstant.Def_Invite_Uid : parentIdStr);
				updata.setHeadImg(randHeadImg());
				updata.setSex(Sex.U);
				
				String myCode = genMyCode("u");
				updata.setMyCode(myCode);
				updata.setNickName(nickName);
				
				String clientIP = ThreadUtil.getClientIP();
				updata.setRegIp(clientIP);
				
//		try{
//			LogUtils.info("nickName %s reg from ip %s", nickName, ip);
//			Area area = areaService.loadCityByIP(ip);
//			if (area != null) {
//				updata.setAreaCode(area.getCode());
//			}
//		} catch (Exception e){
//			LogUtils.warn("error load city for ip %s phone %s", ip, phone);
//		}
				
				doSave(updata);
				balanceService.init(updata.getId());
				
				return updata;
			}
		} catch (Exception e) {
			throw new BaseRuntimeException("REG_ERR", "注册发生错误，请稍后重试", e);
		} finally {
			RedisDistLock.release("register", phone);
		}

		return null;
	}
	
	private String randHeadImg() {
		int headImgCnt = BaseConfig.getInt("headimg.max.cnt", 30);
		int index = RandomUtil.rand(headImgCnt);
		
		return GlobalConstant.STATIC_DOMAIN + "/manual/head/" + index + ".jpg";
	}
	
	/**
	 * 如果myCode是一个手机号，则根据手机号查询；否则根据推荐码查询
	 * @param myCode
	 * @return
	 */
	public RegUser loadByMyCode(String myCode) {
		RegUser query = new RegUser();
		if(RegexUtil.isPhone(myCode)) {
			query.setPhone(EncryptTools.phoneEncypt(myCode));
		} else {
			query.setMyCode(myCode);
		}
		
		return this.findOne(query);
	}
	
	private String genMyCode(String prefix) {
		for(int i = 0; i < 3; i++){
			String myCode = prefix + RandomUtil.randomUpperNumCode(3,5);
			RegUser dbData = this.loadByMyCode(myCode);
			if(dbData == null){
				return myCode;
			}
		}
		
		return null;
	}
	
//	public void updateRealName(int uid, String encyptRealName){
//		User updata = new User();
//		updata.setId(uid);
//		updata.setRealName(encyptRealName);
//		updata.setLastUptime(new Date());
//		
//		this.update(updata);
//	}
	
	/**
	 * @param phone
	 * @param rawPass
	 * @return
	 */
	public boolean resetPwd(String phone, String rawPass){
		phone = EncryptTools.phoneEncypt(phone);
		RegUser dbData = loadByEncyptPhone(phone);
		if(dbData == null){
			throw new BaseRuntimeException("NOT_EXIST", "该手机号不存在");
		}
		String md5pass = encryptPass(rawPass);
		RegUser updata = new RegUser();
		updata.setId(dbData.getId());
		updata.setPasswd(md5pass);
		updata.setLastUptime(new Date());
		
		return this.update(updata);
	}
	
	/**
	 * @param phone
	 * @param rawPass
	 * @return
	 */
	public boolean modifyPwd(int uid, String oldPwd, String newPwd){
		RegUser dbData = this.findById(uid);
		if(dbData == null){
			throw new BaseRuntimeException("NOT_EXIST", "用户不存在");
		}
		String md5OldPwd = encryptPass(oldPwd);
		if(!md5OldPwd.equals(dbData.getPasswd())){
			throw new BaseRuntimeException("ILLEGAL_PWD", "旧密码不正确");
		}
		String md5NewPwd = encryptPass(newPwd);
		RegUser updata = new RegUser();
		updata.setId(dbData.getId());
		updata.setPasswd(md5NewPwd);
		updata.setLastUptime(new Date());
		
		return this.update(updata);
	}
	
	public boolean modifyPwd(int uid, String newPwd) {
		RegUser dbData = this.findById(uid);
		if(dbData == null){
			throw new BaseRuntimeException("NOT_EXIST", "用户不存在");
		}
		String md5NewPwd = encryptPass(newPwd);
		RegUser updata = new RegUser();
		updata.setId(dbData.getId());
		updata.setPasswd(md5NewPwd);
		updata.setLastUptime(new Date());
		
		return this.update(updata);
	}
	
	/**修改手机号*/
	public boolean chgPhone(int uid, String phone){
		RegUser dbData = this.findById(uid);
		if(dbData == null){
			throw new BaseRuntimeException("NOT_EXIST", "数据不存在");
		}
		RegUser updata = new RegUser();
		updata.setId(dbData.getId());
		updata.setPhone(EncryptTools.phoneEncypt(phone));
		updata.setLastUptime(new Date());
		super.update(updata);
		return true;
	}
	
	public RegUser checkLoginInfo(String loginName, String rawPass) {
		RegUser dbData = this.loadByLoginName(loginName);
		if (dbData == null) {
			throw new BaseRuntimeException("NOT_EXIST", "账号不存在", "loginName:" + loginName);
		}
		
		if(!dbData.isNormal()) {
			throw new BaseRuntimeException("ILLEGAL_STATE", "用户状态异常", "loginName:" + loginName);
		}

		String md5pass = encryptPass(rawPass);
		if (!md5pass.equals(dbData.getPasswd())) {
			throw new BaseRuntimeException("ILLEGAL_PASS", "用户名或密码错误");
		}

		return dbData;
	}

	private static String encryptPass(String rawPass) {
		if (StringUtils.isBlank(rawPass)) {
			throw new BaseRuntimeException("ILLEGAL_PARAM", "密码不能为空");
		}
		String md5sum = EncryptTools.encryptByMD5(rawPass);
		String part = md5sum.substring(8, 24);

		return part;
	}

	private RegUser loadByLoginName(String loginName) {
		if (RegexUtil.isPhone(loginName)) {
			String encryptVal = EncryptTools.phoneEncypt(loginName);
			return this.loadByEncyptPhone(encryptVal);
		} 
		throw new BaseRuntimeException("ILLEGAL_PARAM", "不支持的登录名");
	}
	
	public SessionUser convertSessionUser(RegUser user) {
		SessionUser suser = new SessionUser();
//		suser.setEmail(user.getEmail());
		suser.setPhone(user.getPhone());
		suser.setHeadImg(user.getHeadImg());
		suser.setNickName(user.getNickName());
		suser.setMyCode(user.getMyCode());
		suser.setPosterCode(user.getInviteUid());
		suser.setUid(user.getId());
		suser.setUserCls(UserCls.ft);
		suser.addOtherVal(EzConstant.InviteUidKey, user.getInviteUid());

		return suser;
	}
	
	public static SessionUser convertWxChannelUser(ThirdUser thirdUser) {
		SessionUser suser = new SessionUser();
		suser.setHeadImg(thirdUser.getHeadImg());
		suser.setNickName(thirdUser.getNickName());
		suser.setUid(thirdUser.getId());
		suser.setChannelUid(thirdUser.getThirdUid());
		suser.setUserCls(UserCls.ft);
		suser.addOtherVal(EzConstant.InviteUidKey, thirdUser.getInviteUid());
		
		return suser;
	}
	
	public PageData<RegUser> loadMyRecUsers(int uid, String parentIdStr, int currPage) {
		String myRecIdStr = StringUtils.isBlank(parentIdStr) || "0".equals(parentIdStr) ? uid + "" : parentIdStr + "," + uid;
		RegUser query = new RegUser();
		query.addQueryParam("inviteUid", myRecIdStr);
		query.setState(CommonStatus.Normal);
		
		return this.findByPage(query, currPage, 10, "id.desc");
	}


	
    public String doLogin(HttpServletRequest request, HttpServletResponse response, SessionUser suser, boolean remindMe){
		
		String token = LogonUtil.storeSession(request, response, suser, remindMe);
		
		/*try{
			loginLogService.logLoginRecord(channelId);
		} catch (Exception e) {
			LogUtils.warn("insert login log err for uid %d", e, suser.getUid());
		}*/
		
		return token;
	}

	public boolean freeze(Integer... pks){
		if(pks == null || pks.length <= 0) {
			throw new BaseRuntimeException("ILLEGAL_PARAM", "freeze的参数不能为空");
		}

		updateStatus("status", CommonStatus.Freezed.name(), pks);

		return true;
	}


	public boolean softRecover(Integer... pks){
		if(pks == null || pks.length <= 0) {
			throw new BaseRuntimeException("ILLEGAL_PARAM", "softRecover的参数不能为空");
		}
		updateStatus("status", CommonStatus.Normal.name(), pks);

		return true;
	}


	public boolean softDel(Integer... pks){
		if(pks == null || pks.length <= 0) {
			throw new BaseRuntimeException("ILLEGAL_PARAM", "softDel的参数不能为空");
		}
		updateStatus("status", CommonStatus.Deleted.name(), pks);
		return true;
	}
	
}