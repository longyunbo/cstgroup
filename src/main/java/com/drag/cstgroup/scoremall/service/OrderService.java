package com.drag.cstgroup.scoremall.service;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.scoremall.dao.OrderDetailDao;
import com.drag.cstgroup.scoremall.dao.OrderInfoDao;
import com.drag.cstgroup.scoremall.dao.ProductInfoDao;
import com.drag.cstgroup.scoremall.entity.OrderDetail;
import com.drag.cstgroup.scoremall.entity.OrderInfo;
import com.drag.cstgroup.scoremall.entity.ProductInfo;
import com.drag.cstgroup.scoremall.form.OrderDetailForm;
import com.drag.cstgroup.scoremall.form.OrderInfoForm;
import com.drag.cstgroup.scoremall.resp.OrderResp;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserScoreUsedRecordDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserScoreUsedRecord;
import com.drag.cstgroup.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

	@Autowired
	private OrderInfoDao orderInfoDao;
	@Autowired
	private OrderDetailDao orderDetailDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ProductInfoDao productInfoDao;
	@Autowired
	private UserScoreUsedRecordDao userScoreUsedRecordDao;
	
	/**
	 * 有机食品购买下单
	 * @param form
	 * @return
	 */
	@Transactional
	public OrderResp purchase(OrderInfoForm form) {
		log.info("【有机类商品下单传入参数】:{}",JSON.toJSONString(form));
		OrderResp resp = new OrderResp();
		try {
			String orderid = StringUtil.uuid();
			int goodsId = form.getGoodsId();
			String goodsName = form.getGoodsName();
			String type = form.getType();
			//购买总数量
			int number = form.getNumber();
			//消耗总积分
			int score = form.getScore();
			String openid = form.getOpenid();
			String buyName = form.getBuyName();
			String phone = form.getPhone();
			//是否开票
			int isBilling = form.getIsBilling();
			//开票类型
			String billingType = form.getBillingType();
			//开票抬头
			String invPayee = form.getInvPayee();
			//发票内容
			String invContent = form.getInvContent();
			//订单方式（0:快递到家，1:送货上门）
			int orderType = form.getOrderType();
			//收货人
			String receiptName = form.getReceiptName();
			//收货人联系方式
			String receiptTel = form.getReceiptTel();
			//所在区域
			String region = form.getRegion();
			//邮政编码
			String postalcode = form.getPostalcode();
			//地址
			String receiptAddress  = form.getReceiptAddress();
			//消耗总积分
			User user = userDao.findByOpenid(openid);
			ProductInfo goods = productInfoDao.findGoodsDetail(goodsId);
			int uid = user.getId();
			//验证参数
			resp = this.checkParam(user,goods,form);
			String returnCode = resp.getReturnCode();
			if(!returnCode.equals(Constant.SUCCESS)) {
				return resp;
			}
			//插入订单表
			OrderInfo order = new OrderInfo();
			order.setId(order.getId());
			order.setOrderid(orderid);
			order.setGoodsId(goodsId);
			order.setGoodsName(goodsName + "等商品");
			order.setType(type);
			order.setNumber(number);
			order.setScore(score);
			//已付款,包含了减积分逻辑
			order.setOrderstatus(OrderInfo.ORDERSTATUS_SUCCESS);
			order.setUid(uid);
			order.setBuyName(buyName);
			order.setPhone(phone);
			order.setOrderType(orderType);
			order.setDeliverystatus(OrderInfo.STATUS_UNDELIVERY);
			order.setReceiptName(receiptName);
			order.setReceiptTel(receiptTel);
			order.setRegion(region);
			order.setPostalcode(postalcode);
			order.setReceiptAddress(receiptAddress);
			order.setIsBilling(isBilling);
			order.setBillingType(billingType);
			order.setInvPayee(invPayee);
			order.setInvContent(invContent);
			
			order.setCreateTime(new Timestamp(System.currentTimeMillis()));
			order.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			orderInfoDao.save(order);
			
			int nowScore = user.getScore();
			//新增积分使用记录
			UserScoreUsedRecord scoreUsedRecord = new UserScoreUsedRecord();
			scoreUsedRecord.setId(scoreUsedRecord.getId());
			scoreUsedRecord.setUid(uid);
			scoreUsedRecord.setGoodsId(goodsId);
			scoreUsedRecord.setGoodsName(goodsName + "等商品");
			scoreUsedRecord.setType(type);
			scoreUsedRecord.setScore(nowScore);
			scoreUsedRecord.setUsedScore(score);
			scoreUsedRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
			userScoreUsedRecordDao.save(scoreUsedRecord);
			//新增购买人数次数
			this.addSuccTimes(goods);
			
			List<OrderDetailForm> orderList = form.getOrderDetail();
			if(orderList != null && orderList.size() > 0) {
				for(OrderDetailForm detail : orderList) {
					//插入订单详情
					int dGoodsId = detail.getGoodsId();
					String dGoodsName = detail.getGoodsName();
					String dNorms = detail.getNorms();
					int dNumber = detail.getNumber();
					int dScore  = detail.getScore();
					OrderDetail orderDetail = new OrderDetail();
					orderDetail.setId(orderDetail.getId());
					orderDetail.setUid(uid);
					orderDetail.setOrderid(orderid);
					orderDetail.setGoodsId(dGoodsId);
					orderDetail.setGoodsName(dGoodsName);
					orderDetail.setNorms(dNorms);
					orderDetail.setScore(dScore);
					orderDetail.setNumber(dNumber);
					orderDetail.setType(type);
					orderDetail.setCreateTime(new Timestamp(System.currentTimeMillis()));
					orderDetail.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					orderDetailDao.save(orderDetail);
				}
			}else {
				resp.setReturnCode(Constant.ORDERNOTEXISTS);
				resp.setErrorMessage("订单详情不存在，请添加商品!");
				log.error("【有机类商品下单订单参数错误】,{}",JSON.toJSONString(orderList));
				return resp;
			}
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("下单成功!");
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	
	
	/**
	 * 其他不需要购物车的服务类商品购买(旅游服务，企业服务，营养套餐)
	 * 需传入的参数：goodsId：商品编号、type:类型、number:购买数量、score:消耗积分,
	 * openid,buy_name,phone,
	 * @param form
	 * @return
	 */
	@Transactional
	public OrderResp commPurchase(OrderInfoForm form) {
		log.info("【服务类商品下单传入参数】:{}",JSON.toJSONString(form));
		OrderResp resp = new OrderResp();
		try {
			String orderid = StringUtil.uuid();
			int goodsId = form.getGoodsId();
			String goodsName = form.getGoodsName();
			String type = form.getType();
			//营养套餐使用
			String norms = form.getNorms();
			//购买总数量
			int number = form.getNumber();
			//消耗总积分
			int score = form.getScore();
			String openid = form.getOpenid();
			String buyName = form.getBuyName();
			String phone = form.getPhone();
			
			//是否开票
			int isBilling = form.getIsBilling();
			//开票类型
			String billingType = form.getBillingType();
			//开票抬头
			String invPayee = form.getInvPayee();
			//发票内容
			String invContent = form.getInvContent();
			
			
			User user = userDao.findByOpenid(openid);
			ProductInfo goods = productInfoDao.findGoodsDetail(goodsId);
			//验证参数,包含了减积分逻辑
			resp = this.checkParam(user,goods,form);
			String returnCode = resp.getReturnCode();
			if(!returnCode.equals(Constant.SUCCESS)) {
				return resp;
			}
			int uid = user.getId();
			//插入订单表
			OrderInfo order = new OrderInfo();
			order.setId(order.getId());
			order.setOrderid(orderid);
			order.setGoodsId(goodsId);
			order.setGoodsName(goodsName);
			order.setType(type);
			order.setNumber(number);
			order.setScore(score);
			//已付款
			order.setOrderstatus(OrderInfo.ORDERSTATUS_SUCCESS);
			order.setUid(uid);
			order.setBuyName(buyName);
			order.setPhone(phone);
			order.setNorms(norms);
			order.setIsBilling(isBilling);
			order.setBillingType(billingType);
			order.setInvPayee(invPayee);
			order.setInvContent(invContent);
			order.setCreateTime(new Timestamp(System.currentTimeMillis()));
			orderInfoDao.save(order);
			int nowScore = user.getScore();
			//新增积分使用记录
			UserScoreUsedRecord scoreUsedRecord = new UserScoreUsedRecord();
			scoreUsedRecord.setId(scoreUsedRecord.getId());
			scoreUsedRecord.setUid(uid);
			scoreUsedRecord.setGoodsId(goodsId);
			scoreUsedRecord.setGoodsName(goodsName);
			scoreUsedRecord.setType(type);
			scoreUsedRecord.setScore(nowScore);
			scoreUsedRecord.setUsedScore(score);
			scoreUsedRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
			userScoreUsedRecordDao.save(scoreUsedRecord);
			//新增购买人数次数
			this.addSuccTimes(goods);
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("下单成功!");
		return resp;
	}
	
	/**
	 * 减积分
	 * @param user
	 * @param socre
	 * @return
	 */
	public Boolean delScore(User user, int socre) {
		boolean flag = false;
		int uScore = user.getScore();
		if (uScore - socre < 0) {
			// 积分不足
			flag = false;
		} else {
			flag = true;
			int nowScore = uScore - socre;
			user.setScore(nowScore);
			userDao.saveAndFlush(user);
		}
		return flag;
	}
	
	/**
	 * 增加购买次数
	 * @param goods
	 * @param number
	 */
	public void addSuccTimes(ProductInfo goods) {
		int succTimes = goods.getSuccTimes();
		goods.setSuccTimes(succTimes + 1);
		productInfoDao.saveAndFlush(goods);
	}
	
	/**
	 * 验证参数
	 * @param user
	 * @param goods
	 * @param form
	 * @return
	 */
	public OrderResp checkParam(User user,ProductInfo goods,OrderInfoForm form) {
		OrderResp resp = new OrderResp();
		int goodsId = form.getGoodsId();
		String openid = form.getOpenid();
		//消耗总积分
		int score = form.getScore();
		if(user != null) {
			String phone = user.getMobile();
			String realName = user.getRealname();
			if(StringUtil.isEmpty(phone) && StringUtil.isEmpty(realName)) {
				resp.setReturnCode(Constant.USERINFO_OVER);
				resp.setErrorMessage("用户信息不完善!");
				log.error("【用户信息不完善】，openid={}",openid);
				return resp;
			}
		}else {
			resp.setReturnCode(Constant.USERNOTEXISTS);
			resp.setErrorMessage("用户不存在!");
			log.error("【用户不存在】，openid={}",openid);
			return resp;
		}
		if(goods == null) {
			resp.setReturnCode(Constant.PRODUCTNOTEXISTS);
			resp.setErrorMessage("商品不存在!");
			log.error("【商品不存在】，goodsId={}",goodsId);
			return resp;
			
		}
		Boolean flag = this.delScore(user, score);
		if(!flag) {
			resp.setReturnCode(Constant.SCORE_NOTENOUGH);
			resp.setErrorMessage("用户积分不足!");
			log.error("【该用户积分不足】,openid:{}",openid);
			return resp;
		}
		resp.setReturnCode(Constant.SUCCESS);
		resp.setErrorMessage("验证通过！");
		return resp;
	}
	
}
