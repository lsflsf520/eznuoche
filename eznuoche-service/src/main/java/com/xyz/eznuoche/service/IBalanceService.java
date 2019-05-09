package com.xyz.eznuoche.service;

import com.xyz.eznuoche.entity.Balance;
import com.xyz.eznuoche.entity.PayLog;
import com.xyz.tools.common.constant.MsgType;

public interface IBalanceService {
	
	void init(int uid);
	
	Balance findById(Integer pk);

	void minusBalance(int targetUid, MsgType msgType, int uid);
	
	void addBalance(PayLog payLog, String thirdTradeNo,  MsgType msgType, int num);
	
}
