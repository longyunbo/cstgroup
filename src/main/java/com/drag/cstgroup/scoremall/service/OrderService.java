package com.drag.cstgroup.scoremall.service;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.scoremall.dao.OrderInfoDao;
import com.drag.cstgroup.scoremall.entity.OrderInfo;
import com.drag.cstgroup.scoremall.form.OrderDetailForm;
import com.drag.cstgroup.scoremall.form.OrderInfoForm;
import com.drag.cstgroup.scoremall.resp.OrderResp;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

	@Autowired
	private OrderInfoDao orderInfoDao;

	
	/**
	 * 购买下单
	 * @param form
	 * @return
	 */
	@Transactional
	public OrderResp purchase(OrderInfoForm form) {
		OrderResp resp = new OrderResp();
		try {
			OrderInfo order = new OrderInfo();
			List<OrderDetailForm> orderDetail = form.getOrderDetail();
			String orderid = StringUtil.uuid();
			if(orderDetail != null && orderDetail.size() > 0) {
				BeanUtils.copyProperties(form, order,new String[]{"createTime", "updateTime"});
				order.setOrderid(orderid);
				order.setCreateTime(new Timestamp(System.currentTimeMillis()));
				orderInfoDao.save(order);
			}else {
				resp.setReturnCode(Constant.ORDERNOTEXISTS);
				resp.setErrorMessage("订单详情不存在，请添加商品！");
				return resp;
			}
			
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	
	
}
