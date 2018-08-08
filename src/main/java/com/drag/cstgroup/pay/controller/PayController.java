package com.drag.cstgroup.pay.controller;

import javax.servlet.http.HttpServletRequest;

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

import com.alibaba.fastjson.JSONObject;
import com.drag.cstgroup.common.BaseResponse;
import com.drag.cstgroup.pay.PayResult;
import com.drag.cstgroup.pay.Sign;
import com.drag.cstgroup.pay.Xiadan;
import com.drag.cstgroup.pay.form.PayForm;
import com.drag.cstgroup.pay.service.PayService;


@RestController
@RequestMapping(value = "/cstgroup/pay")
public class PayController {
	
	@Autowired
	PayService payService;
	
	private final static Logger log = LoggerFactory.getLogger(PayController.class);
	
	@RequestMapping(value = "/wxpay", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<JSONObject> wxPay1(HttpServletRequest request,@RequestParam(required = true) String openid,int price) {
		JSONObject Json = Xiadan.wxPay(request,openid,price);
		return new ResponseEntity<JSONObject>(Json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/signagain", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<JSONObject> signAgain(@RequestParam(required = true) String repay_id) {
		JSONObject Json = Sign.signAgain(repay_id);
		return new ResponseEntity<JSONObject>(Json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/payresult", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<String> payResult(HttpServletRequest request) {
		String Json = PayResult.payResult(request);
		return new ResponseEntity<String>(Json, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/paycomplete", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<BaseResponse> payComplete(@RequestBody PayForm form) {
		BaseResponse resp = payService.payComplete(form);
		return new ResponseEntity<BaseResponse>(resp, HttpStatus.OK);
	}
}
