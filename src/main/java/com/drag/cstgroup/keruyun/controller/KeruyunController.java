package com.drag.cstgroup.keruyun.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.drag.cstgroup.keruyun.service.KeruyunService;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.entity.User;


@RestController
@RequestMapping(value = "/cstgroup/keruyun")
public class KeruyunController {
	
	@Autowired
	KeruyunService keruyunService;
	@Autowired
	UserDao userDao;
	
	private final static Logger log = LoggerFactory.getLogger(KeruyunController.class);
	
	@RequestMapping(value = "/getuser", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<JSONObject> getUser(@RequestParam String openid) {
		JSONObject Json = null;
		User us = userDao.findByOpenid(openid);
		if(us != null ) {
			String customerId = us.getCustomerId();
			Json = keruyunService.getCustomerDetailByCustomerId(customerId);
		}
		return new ResponseEntity<JSONObject>(Json, HttpStatus.OK);
	}
	
}
