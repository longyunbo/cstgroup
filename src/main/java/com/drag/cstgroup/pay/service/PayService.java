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
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PayService {

	@Autowired
	private UserDao userDao;
	@Autowired
	private RechargeRecordDao rechargeRecordDao;
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
			//微信商户订单号
			String outTradeNo = form.getOutTradeNo();
			User us = userDao.findByOpenid(openid);
			if(us == null) {
				resp.setReturnCode(Constant.FAIL);
				resp.setErrorMessage("该用户不存在!");
				log.error("【用户信息不存在】{}:openid = ",openid);
				return resp;
			}
			int uid = us.getId();
			//赠送的金额=充值的金额/10
			BigDecimal extraBalance = price.divide(new BigDecimal(10));
			//额外赠送的余额
			BigDecimal extra_balance = us.getExtraBalance();
			//余额
			BigDecimal balance = us.getBalance();
			//赠送余额增加
			extra_balance = extra_balance.add(extraBalance);
			//余额增加
			balance = balance.add(price).add(extraBalance);
//			//新增充值记录
			UserRechargeRecord record = new UserRechargeRecord();
			record.setId(record.getId());
			record.setUid(uid);
			record.setBalance(balance);
			record.setExtraBalance(extra_balance);
			record.setRechargeBalance(price);
			record.setOutTradeNo(outTradeNo);
			record.setStatus(UserRechargeRecord.STATUS_FAIL);
			record.setCreateTime(new Date(System.currentTimeMillis()));
			rechargeRecordDao.save(record);
			
			//充值完成校验客如云的余额和drag库的客户余额
			BigDecimal totalAddBanlance = price.add(extraBalance);
			form.setPrice(totalAddBanlance);
			PayResp presp = keruyunService.recharge(form);
			String returnCode = presp.getReturnCode();
			if(!Constant.SUCCESS.equals(returnCode)) {
				resp.setReturnCode(Constant.RECHARGE_ERROR);
				resp.setErrorMessage("该用户充值异常，请联系客服!");
				log.error("【用户充值异常】:openid = {},presp = {}",presp);
				return resp;
			}else {
				String kryBalance = presp.getBalance();
				if(!StringUtil.isEmpty(kryBalance)) {
					BigDecimal rechargeBalance = us.getRechargeBalance();
					rechargeBalance = rechargeBalance.add(price);
					int totalRechargeBalance = rechargeBalance.intValue();
					//0-普通会员，1-银卡，2-金卡，3-铂金卡
					if(totalRechargeBalance<5000) {
						us.setRankLevel(0);
					}else if(totalRechargeBalance >= 5000 && totalRechargeBalance < 10000) {
						us.setRankLevel(1);
					}else if(totalRechargeBalance >= 10000 && totalRechargeBalance < 20000) {
						us.setRankLevel(2);
					}else {
						us.setRankLevel(3);
					}
					//更新用户数据
					us.setExtraBalance(extra_balance);
					us.setBalance(new BigDecimal(kryBalance));
					us.setRechargeBalance(rechargeBalance);
					userDao.saveAndFlush(us);
				}else {
					resp.setReturnCode(Constant.RECHARGE_ERROR);
					resp.setErrorMessage("该用户充值异常，请联系客服!");
					log.error("【用户充值异常】:openid = {},presp = {}",openid,presp);
					return resp;
				}
			}
			
			record.setStatus(UserRechargeRecord.STATUS_SUCC);
			rechargeRecordDao.saveAndFlush(record);
			
		} catch (Exception e) {
			log.error("【充值异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("充值成功!");
		return resp;
	}
	
	
}
