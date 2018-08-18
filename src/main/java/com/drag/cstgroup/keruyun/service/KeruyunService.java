package com.drag.cstgroup.keruyun.service;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.drag.cstgroup.utils.MD5Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KeruyunService {
	@Autowired
	private UserDao userDao;
	
	private static String keruyunurl;
	private static String shopIdenty;
	private static String appKey;
	private static String secretKey;
	private static String appKeyDc;
	private static String secretKeyDc;
	private static String tpId;
	
	@Value("${keruyun.url.appKey}")
    public void seAppKey(String value) {
		appKey = value;
    }
	@Value("${keruyun.url.secretKey}")
    public void setSecretKey(String value) {
		secretKey = value;
    }
	@Value("${keruyun.url.dc.appKey}")
    public void seAppKeyDc(String value) {
		appKeyDc = value;
    }
	@Value("${keruyun.url.dc.secretKey}")
    public void setSecretKeyDc(String value) {
		secretKeyDc = value;
    }
	@Value("${keruyun.url.url}")
    public void setKeruyunurl(String value) {
		keruyunurl = value;
    }
	@Value("${keruyun.url.shopIdenty}")
    public void setShopIdenty(String value) {
		shopIdenty = value;
    }
	@Value("${keruyun.url.tpId}")
    public void setTpId(String value) {
		tpId = value;
    }
	
	
	/**
	 * 调用客如云的创建用户接口
	 * @param form
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public UserResp createCustomer(UserForm form) {
		log.info("【客如云更新用户，传入参数】UserForm:{}",JSON.toJSONString(form));
		UserResp resp = new UserResp();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/crm/createCustomer?%s",keruyunurl,commParam);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date birthday;
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
	 * 用户登录
	 * @param openid
	 * @return
	 */
	public JSONObject login(String openid){
		log.info("【客如云用户登录，传入参数】openid:{}",openid);
		JSONObject resp = new JSONObject();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/crm/login?%s",keruyunurl,commParam);
			JSONObject json = new JSONObject();
			User us = userDao.findByOpenid(openid);
			json.put("loginId", us.getMobile());
			json.put("loginType", 0);
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
	 * 根据编号获取用户
	 * @param openid
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	public JSONObject getCustomerDetailById(String openid){
		log.info("【客如云查询用户，传入参数】openid:{}",openid);
		JSONObject resp = new JSONObject();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/crm/getCustomerDetailById?%s",keruyunurl,commParam);
			JSONObject json = new JSONObject();
			User us = userDao.findByOpenid(openid);
			json.put("customerId", us.getCustomerId());
			log.info("【客如云用户查询传入参数】:json={}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云用户查询返回参数】:httpresult={}",httpresult);
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
	 * 客如云充值接口
	 * @param form
	 * @return
	 */
	public PayResp recharge(PayForm form) {
		PayResp resp = new PayResp();
		try {
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/crm/member/recharge?%s",keruyunurl,commParam);
			log.info("【客如云会员充值传入参数】:PayForm= {}",JSON.toJSONString(form));
			JSONObject json = new JSONObject();
			String openid = form.getOpenid();
			BigDecimal price = form.getPrice();
			String outTradeNo = form.getOutTradeNo();
			User us = userDao.findByOpenid(openid);
			
			json.put("customerId", us.getCustomerId());
			json.put("cardType", 1);
			json.put("businessType",1);
			//单位为 分
			json.put("amount", price.multiply(new BigDecimal(100)));
			json.put("tpOrderId", outTradeNo);
			log.info("【客如云会员充值传入参数】:json={}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云会员充值返回参数】:httpresult={}",httpresult);
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
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/crm/point/add?%s",keruyunurl,commParam);
			log.info("【客如云会员增加积分传入参数】:createCustomerUrl {}",createCustomerUrl);
			JSONObject json = new JSONObject();
			json.put("customerId", customerId);
			json.put("points", score);
			json.put("remark", remark);
			json.put("businessType",4);
			log.info("【客如云会员增加积分传入参数】:json= {}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云会员增加积分返回参数】:httpresult= {}",httpresult);
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
			String commParam = SignUtil.getComParam(appKey,secretKey);
			String createCustomerUrl = String.format("%s/open/v1/crm/point/cut?%s",keruyunurl,commParam);
			log.info("【客如云会员扣减积分传入参数】:createCustomerUrl= {}",createCustomerUrl);
			JSONObject json = new JSONObject();
			json.put("customerId", customerId);
			json.put("points", score);
			json.put("businessType",4);
			json.put("remark", remark);
			log.info("【客如云会员扣减积分传入参数】:json= {}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云会员扣减积分返回参数】:httpresult= {}",httpresult);
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
	
	/**
	 * 快餐下单
	 * @param form
	 * @return
	 */
	public PayResp createOrder(PayForm form) {
		PayResp resp = new PayResp();
		try {
			String commParam = SignUtil.getComParam(appKeyDc,secretKeyDc);
			String createCustomerUrl = String.format("%s/open/v1/snack/order/create?%s",keruyunurl,commParam);
			log.info("【客如云快餐下单传入参数】:PayForm= {}",JSON.toJSONString(form));
			log.info("【客如云快餐下单传入参数】:createCustomerUrl= {}",createCustomerUrl);
			String openid = form.getOpenid();
			BigDecimal price = form.getPrice();
			String tpOrderId = form.getTpOrderId();
			User us = userDao.findByOpenid(openid);
			//转换为分
			price = price.multiply(new BigDecimal(100));
			Date now = new Date(System.currentTimeMillis());
			JSONObject json = new JSONObject();
			json.put("tpOrderId", tpOrderId);
			json.put("shopIdenty", shopIdenty);
			json.put("shopName", "积分商城");
			json.put("peopleCount", 1);
			//订单总价为元
			json.put("totalPrice", price);
			json.put("status", 2);
			json.put("createTime", now.getTime());
			json.put("updateTime", now.getTime());
			
			JSONArray products = new JSONArray();
			JSONObject jsonPro = new JSONObject();
			jsonPro.put("tpId", tpId);
			jsonPro.put("name", "水煮牛肉");
			jsonPro.put("unit", "1");
			jsonPro.put("quantity", "1");
			jsonPro.put("price", price);
			jsonPro.put("totalFee", price);
			products.add(jsonPro);
		
			//支付信息单位都为分
			JSONObject jsonPay = new JSONObject();
			jsonPay.put("memberId", us.getCustomerId());
			jsonPay.put("memberPassword", MD5Utils.encrypt("888888"));
			//优惠总金额 优惠总金额=合作方承担优惠总金额+商家承担优惠总金额,单位：分
			jsonPay.put("totalDiscountFee", "0");
			//平台优惠总金额，单位：分
			jsonPay.put("platformDiscountFee", "0");
			//传discountDetails时:商家优惠总金额=商家优惠+客如云优惠券总额 单位：分 不传discountDetails时: 商家优惠
			jsonPay.put("shopDiscountFee", "0");
			//服务费 平台向商家收取的抽佣金额，单位：分
			jsonPay.put("serviceFee", "0");
			//订单总价=商品总额,单位是分，单位：分
			jsonPay.put("totalFee", price);
			//用户实付总价 顾客实际付款金额=订单总价-活动优惠总金额，单位：分
			jsonPay.put("userFee", price);
			//商户实收总价 商户实际收款=订单总价-商家承担优惠金额-服务费，单位：分
			jsonPay.put("shopFee", price);
			//支付方式 1:会员卡余额 
			jsonPay.put("payType", "1");
			
			JSONArray customers = new JSONArray();
			JSONObject jsonCust = new JSONObject();
			jsonCust.put("id", us.getCustomerId());
			jsonCust.put("phoneNumber", us.getMobile());
			jsonCust.put("name", us.getRealname());
			jsonCust.put("gender", us.getSex());
			customers.add(jsonCust);
			
			json.put("products", products);
			json.put("payment", jsonPay);
			json.put("customers", customers);
			
			log.info("【客如云快餐下单传入参数】:json={}",json);
			String httpresult = HttpsUtil.doPost(createCustomerUrl, json.toString(), "utf-8");
			log.info("【客如云快餐下单返回参数】:result={}",httpresult);
			if (httpresult != null) {
				JSONObject jsonResult = JSON.parseObject(httpresult);
				int code = jsonResult.getInteger("code");
				String message = jsonResult.getString("message");
				JSONObject result = (JSONObject) jsonResult.get("result");
				if (code == 0) {
					//成功
					String orderId = result.getString("orderId");
					resp.setOrderId(orderId);
					resp.setReturnCode(Constant.SUCCESS);
					resp.setErrorMessage("客如云下单成功！");
					return resp;
				} else {
					resp.setReturnCode(String.valueOf(code));
					resp.setErrorMessage(message);
					log.error("【下单，异常】jsonResult:{}",JSON.toJSONString(jsonResult));
					return resp;
				}
			}
		} catch (Exception e) {
			log.error("【下单异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("下单成功!");
		return resp;
	}
	
}
