package com.drag.cstgroup.pay.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.drag.cstgroup.common.BaseResponse;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.keruyun.service.KeruyunService;
import com.drag.cstgroup.pay.dao.RechargeRecordDao;
import com.drag.cstgroup.pay.entity.UserRechargeRecord;
import com.drag.cstgroup.pay.form.PayForm;
import com.drag.cstgroup.pay.resp.PayResp;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserScoreRecordDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserScoreRecord;
import com.drag.cstgroup.user.resp.ScoreResp;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private RechargeRecordDao rechargeRecordDao;
	@Autowired
	private UserScoreRecordDao userScoreRecordDao;
	@Autowired
	private KeruyunService keruyunService;
	
	
	/**
	 * 会员充值完成调用接口
	 * 传用户的openid，和充值金额(元)
	 * @param openid
	 * @param price
	 * @return
	 */
	@Transactional
	public BaseResponse payComplete(PayForm form) {
		BaseResponse resp = new BaseResponse();
		try {
			log.info("【会员充值传入参数】:PayForm= {}",JSON.toJSONString(form));
			String openid = form.getOpenid();
			BigDecimal price = form.getPrice();
			String outTradeNo = form.getOutTradeNo();
			User us = userDao.findByOpenid(openid);
			if(us == null) {
				resp.setReturnCode(Constant.FAIL);
				resp.setErrorMessage("该用户不存在!");
				log.error("【用户信息不存在】{}:openid = ",openid);
				return resp;
			}
			String customerId  = us.getCustomerId();
			int uid = us.getId();
			BigDecimal balance = us.getBalance();
			BigDecimal extra_balance = us.getExtraBalance();
			//赠送的金额=充值的金额/10
			BigDecimal extraBalance = price.divide(new BigDecimal(10));
			balance = balance.add(price);
			extra_balance = extra_balance.add(extraBalance); 
//			int uscore = us.getScore();
//			int nowScore = uscore + score;
//			//新增充值记录
			UserRechargeRecord record = new UserRechargeRecord();
			record.setId(record.getId());
			record.setUid(uid);
			record.setBalance(balance);
			record.setRechargeBalance(price);
			record.setExtraBalance(extraBalance);
			record.setOutTradeNo(outTradeNo);
			record.setStatus(UserRechargeRecord.STATUS_FAIL);
			record.setCreateTime(new Date(System.currentTimeMillis()));
			rechargeRecordDao.save(record);
			
			//充值完成校验客如云的余额和drag库的客户余额
			PayResp presp = keruyunService.recharge(form);
			String returnCode = presp.getReturnCode();
			if(Constant.SUCCESS.equals(returnCode)) {
				String kryBalance = presp.getBalance();
				String myBalance = balance.toString();
//				//充值完成校验客如云的积分和drag库的客户积分
//				ScoreResp sresp = keruyunService.addScore(customerId, score);
//				String sreturnCode = sresp.getReturnCode();
//				if(!Constant.SUCCESS.equals(sreturnCode)) {
//					resp.setReturnCode(Constant.RECHARGE_ERROR);
//					resp.setErrorMessage("该用户充值异常，请联系客服!");
//					log.error("【用户积分增加异常】:sresp = {}",JSON.toJSONString(sresp));
//					return resp;
//				}
//				String kryCurrentPoints = sresp.getCurrentPoints();
//				if(!kryCurrentPoints.equals(String.valueOf(nowScore))) {
//					resp.setReturnCode(Constant.RECHARGE_ERROR);
//					resp.setErrorMessage("该用户充值异常，请联系客服!");
//					log.error("【用户积分增加异常】:openid = {},kryBalance = {},myBalance = {}",openid,kryBalance,myBalance);
//					return resp;
//				}
				
				if(!myBalance.equals(kryBalance)) {
					resp.setReturnCode(Constant.RECHARGE_ERROR);
					resp.setErrorMessage("该用户充值异常，请联系客服!");
					log.error("【用户充值异常】:openid = {},kryBalance = {},myBalance = {}",openid,kryBalance,myBalance);
					return resp;
				}
			}else {
				resp.setReturnCode(Constant.FAIL);
				resp.setErrorMessage(presp.getErrorMessage());
				return resp;
			}
			
			//更新用户数据
			us.setBalance(balance);
			
//			us.setScore(nowScore);
			userDao.saveAndFlush(us);
			
			record.setStatus(UserRechargeRecord.STATUS_SUCC);
			rechargeRecordDao.saveAndFlush(record);
			
			//新增积分记录
			UserScoreRecord scoreRecord = new UserScoreRecord();
			scoreRecord.setId(scoreRecord.getId());
			scoreRecord.setUid(uid);
			scoreRecord.setGoodsId(0);
			scoreRecord.setGoodsName("会员充值" + price);
			scoreRecord.setType(UserScoreRecord.TYPE_RECHARGE);
//			scoreRecord.setScore(nowScore);
//			scoreRecord.setAvailableScore(score);
			scoreRecord.setCreateTime(new Date(System.currentTimeMillis()));
			userScoreRecordDao.save(scoreRecord);
		} catch (Exception e) {
			log.error("【充值异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("充值成功!");
		return resp;
	}
	
	
}
