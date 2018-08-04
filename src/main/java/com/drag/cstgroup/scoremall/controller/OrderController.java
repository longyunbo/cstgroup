package com.drag.cstgroup.scoremall.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drag.cstgroup.scoremall.form.OrderInfoForm;
import com.drag.cstgroup.scoremall.resp.OrderResp;
import com.drag.cstgroup.scoremall.service.OrderService;



@RestController
@RequestMapping(value = "/cstgroup/order", produces = "application/json;charset=utf-8")
public class OrderController {
	
	private final static Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private OrderService orderInfoService;
	
	/**
	 * 服务类商品购买(旅游服务，企业服务，营养套餐)
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/commpurchase", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<OrderResp> commpurchase(@RequestBody OrderInfoForm form) {
		OrderResp detailVo = orderInfoService.commPurchase(form);
		return new ResponseEntity<OrderResp>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 有机食品购买下单
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/yjpurchase", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<OrderResp> purchase(@RequestBody OrderInfoForm form) {
		OrderResp detailVo = orderInfoService.purchase(form);
		return new ResponseEntity<OrderResp>(detailVo, HttpStatus.OK);
	}
	
	
}
