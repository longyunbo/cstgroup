package com.drag.cstgroup.scoremall.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drag.cstgroup.scoremall.form.OrderInfoForm;
import com.drag.cstgroup.scoremall.resp.OrderResp;
import com.drag.cstgroup.scoremall.service.OrderService;
import com.drag.cstgroup.scoremall.vo.OrderDetailVo;
import com.drag.cstgroup.scoremall.vo.OrderInfoVo;



@RestController
@RequestMapping(value = "/cstgroup/order")
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
	
	/**
	 * 开发票
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/openbill", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<OrderResp> openBill(@RequestBody OrderInfoForm form) {
		OrderResp detailVo = orderInfoService.openBill(form);
		return new ResponseEntity<OrderResp>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 获取我的订单
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/myorders", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderInfoVo>> myorders(@RequestParam(required = true)  String openid,@RequestParam String type) {
		List<OrderInfoVo> list = orderInfoService.myOrders(openid,type);
		return new ResponseEntity<List<OrderInfoVo>>(list, HttpStatus.OK);
	}
	
	/**
	 * 订单详情
	 * @param orderid
	 * @return
	 */
	@RequestMapping(value = "/orderdetail", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderDetailVo>> orderDetail(@RequestParam(required = true)  String orderid) {
		List<OrderDetailVo> list = orderInfoService.orderDetail(orderid);
		return new ResponseEntity<List<OrderDetailVo>>(list, HttpStatus.OK);
	}
	
	
	
	/**
	 * 获取我的发票
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/mybills", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<OrderInfoVo>> mybills(@RequestParam(required = true)  String openid) {
		List<OrderInfoVo> list = orderInfoService.myBill(openid);
		return new ResponseEntity<List<OrderInfoVo>>(list, HttpStatus.OK);
	}
	
}
