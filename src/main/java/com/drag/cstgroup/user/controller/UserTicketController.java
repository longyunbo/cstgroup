package com.drag.cstgroup.user.controller;

import java.util.List;

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

import com.drag.cstgroup.user.resp.UserTicketResp;
import com.drag.cstgroup.user.service.UserTicketService;
import com.drag.cstgroup.user.vo.UserTicketVo;


@RestController
@RequestMapping(value = "/chirouosopark/ticket")
public class UserTicketController {
	
	private final static Logger log = LoggerFactory.getLogger(UserTicketController.class);

	@Autowired
	private UserTicketService userTicketService;
	
	/**
	 * 获取卡券列表
	 * @return
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<UserTicketVo>> listTicket(@RequestParam(required = true) String openid) {
		List<UserTicketVo> rows= userTicketService.listTicket(openid);
		return new ResponseEntity<List<UserTicketVo>>(rows, HttpStatus.OK);
	}
	
	/**
	 * 核销卡券
	 * @param ticketId
	 * @return
	 */
	@RequestMapping(value = "/destory", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserTicketResp> destory(@RequestParam(required = true) int ticketId) {
		UserTicketResp resp = userTicketService.destoryTicket(ticketId);
		return new ResponseEntity<UserTicketResp>(resp, HttpStatus.OK);
	}
	
	
}
