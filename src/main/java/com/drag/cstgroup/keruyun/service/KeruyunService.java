package com.drag.cstgroup.keruyun.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.keruyun.util.SignUtil;
import com.drag.cstgroup.pay.form.PayForm;
import com.drag.cstgroup.pay.resp.PayResp;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.form.UserForm;
import com.drag.cstgroup.user.resp.ScoreResp;
import com.drag.cstgroup.user.resp.UserResp;
import com.drag.cstgroup.utils.HttpsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KeruyunService {
	@Autowired
	private UserDao userDao;
	
	private static String keruyunurl;
	@Value("${keruyun.url.url}")
    public void setKeruyunurl(String value) {
		keruyunurl = value;
    }
	private static String commParam = "";
	
	
	/**
	 * 调用客如云的创建用户接口
	 * @param form
	 * @return
	 */
	public UserResp createCustomer(UserForm form) {
		log.info("【客如云更新用户，传入参数】UserForm:{}",JSON.toJSONString(form));
		UserResp resp = new UserResp();
		commParam = SignUtil.getComParam();
		String createCustomerUrl = String.format("%s/open/v1/crm/createCustomer?%s",keruyunurl,commParam);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date birthday;
		try {
			birthday = formatter.parse(form.getBirthday());
			Date now = new Date(System.currentTimeMillis());
			JSONObject json = new JSONObject();
			json.put("loginId", form.getMobile());
			json.put("loginType", 0);
			json.put("birthday",birthday.getTime());
			json.put("sex", form.getSex());
			json.put("name", form.getRealname());
			json.put("attentionWxTime", now.getTime());
			
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				int code = jsonResult.getInteger("code");
				String message = jsonResult.getString("message");
				JSONObject result = (JSONObject) jsonResult.get("result");
				if (code == 0) {
					//成功
					String customerId = result.getString("customerId");
					String customerMainId = result.getString("customerMainId");
					resp.setCustomerId(customerId);
					resp.setCustomerMainId(customerMainId);
					
					//升级会员
					String createOrUpgradeMemberUrl = String.format("%s/open/v1/crm/createOrUpgradeMember?%s",keruyunurl,commParam);
					JSONObject mjson = new JSONObject();
					mjson.put("attentionWxTime", now.getTime());
					mjson.put("birthday",birthday.getTime());
					mjson.put("consumePwd", "888888");
					mjson.put("customerId", customerId);
					mjson.put("customerMainId", customerMainId);
					mjson.put("loginId", form.getMobile());
					mjson.put("loginType", 0);
					mjson.put("name", form.getRealname());
					mjson.put("sex", form.getSex());
					String mresult = HttpsUtil.doPost(createOrUpgradeMemberUrl, mjson.toString(), "utf-8");
					JSONObject mjsonResult = JSON.parseObject(mresult);
					log.info("【升级会员，】jsonResult:{}",JSON.toJSONString(mjsonResult));
					
					resp.setReturnCode(Constant.SUCCESS);
					return resp;
				} else {
					resp.setReturnCode(String.valueOf(code));
					resp.setErrorMessage(message);
					log.error("【更新用户，异常】jsonResult:{}",JSON.toJSONString(jsonResult));
					return resp;
				}
			}
		} catch (Exception e) {
			log.error("【更新用户，异常】e:{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	

	
	/**
	 * 根据编号获取
	 * @param openid
	 * @return
	 */
	public JSONObject getCustomerDetailById(String openid) {
		log.info("【客如云查询用户，传入参数】openid:{}",openid);
		JSONObject resp = new JSONObject();
		commParam = SignUtil.getComParam();
		String createCustomerUrl = String.format("%s/open/v1/crm/getCustomerDetailById?%s",keruyunurl,commParam);
		try {
			JSONObject json = new JSONObject();
			User us = userDao.findByOpenid(openid);
			json.put("customerId", us.getCustomerId());
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				resp = (JSONObject) jsonResult.get("result");
			}
		} catch (Exception e) {
			log.error("【查询用户，异常】e:{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	
	/**
	 * 充值完成，调用客如云充值接口
	 * @param form
	 * @return
	 */
	public PayResp recharge(PayForm form) {
		PayResp resp = new PayResp();
		try {
			commParam = SignUtil.getComParam();
			String createCustomerUrl = String.format("%s/open/v1/crm/member/recharge?%s",keruyunurl,commParam);
			log.info("【客如云会员充值传入参数】:PayForm= {}",JSON.toJSONString(form));
			JSONObject json = new JSONObject();
			String openid = form.getOpenid();
			BigDecimal price = form.getPrice();
			String outTradeNo = form.getOutTradeNo();
			User us = userDao.findByOpenid(openid);
			
			json.put("customerId", us.getCustomerId());
			json.put("cardType", 2);
			json.put("businessType",1);
			//单位为 分
			json.put("amount", price.multiply(new BigDecimal(100)));
			json.put("tpOrderId", outTradeNo);
			log.info("【客如云会员充值传入参数】:json={}",json);
			
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				int code = jsonResult.getInteger("code");
				String message = jsonResult.getString("message");
				JSONObject result = (JSONObject) jsonResult.get("result");
				if (code == 0) {
					//成功
					String balance = result.getString("balance");
					resp.setReturnCode(Constant.SUCCESS);
					resp.setBalance(balance);
					return resp;
				} else {
					resp.setReturnCode(String.valueOf(code));
					resp.setErrorMessage(message);
					log.error("【充值，异常】jsonResult:{}",JSON.toJSONString(jsonResult));
					return resp;
				}
			}
		} catch (Exception e) {
			log.error("【充值异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("充值成功!");
		return resp;
	}
	
	/**
	 * 会员增加积分
	 * @param openid
	 * @param score
	 * @return
	 */
	public ScoreResp addScore(String customerId,int score,String remark) {
		ScoreResp resp = new ScoreResp();
		try {
			commParam = SignUtil.getComParam();
			String createCustomerUrl = String.format("%s/open/v1/crm/point/add?%s",keruyunurl,commParam);
			log.info("【客如云会员增加积分传入参数】:customerId= {},score= {}",customerId,score);
			JSONObject json = new JSONObject();
			json.put("customerId", customerId);
			json.put("points", score);
			json.put("remark", remark);
			json.put("businessType",4);
			
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				int code = jsonResult.getInteger("code");
				String message = jsonResult.getString("message");
				JSONObject result = (JSONObject) jsonResult.get("result");
				if (code == 0) {
					//成功
					String currentPoints = result.getString("currentPoints");
					resp.setReturnCode(Constant.SUCCESS);
					resp.setCurrentPoints(currentPoints);
					return resp;
				} else {
					resp.setReturnCode(String.valueOf(code));
					resp.setErrorMessage(message);
					log.error("【会员增加积分，异常】jsonResult:{}",JSON.toJSONString(jsonResult));
					return resp;
				}
			}
		} catch (Exception e) {
			log.error("【会员增加积分异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("积分增加成功!");
		return resp;
	}
	
	/**
	 * 积分扣减
	 * @param openid
	 * @param score
	 * @return
	 */
	public ScoreResp cutScore(String customerId,int score,String remark) {
		ScoreResp resp = new ScoreResp();
		try {
			commParam = SignUtil.getComParam();
			String createCustomerUrl = String.format("%s/open/v1/crm/point/cut?%s",keruyunurl,commParam);
			log.info("【客如云会员扣减积分传入参数】:customerId= {},score= {}",customerId,score);
			JSONObject json = new JSONObject();
			json.put("customerId", customerId);
			json.put("points", score);
			json.put("businessType",4);
			json.put("remark", remark);
			
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				int code = jsonResult.getInteger("code");
				String message = jsonResult.getString("message");
				JSONObject result = (JSONObject) jsonResult.get("result");
				if (code == 0) {
					//成功
					String currentPoints = result.getString("currentPoints");
					resp.setReturnCode(Constant.SUCCESS);
					resp.setCurrentPoints(currentPoints);
					return resp;
				} else {
					resp.setReturnCode(String.valueOf(code));
					resp.setErrorMessage(message);
					log.error("【会员扣减积分，异常】jsonResult:{}",JSON.toJSONString(jsonResult));
					return resp;
				}
			}
		} catch (Exception e) {
			log.error("【会员扣减积分异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("积分扣减成功!");
		return resp;
	}
	
	
	
}
