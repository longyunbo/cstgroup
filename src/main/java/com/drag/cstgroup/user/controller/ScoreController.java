package com.drag.cstgroup.user.controller;

import java.math.BigDecimal;
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

import com.drag.cstgroup.common.BaseResponse;
import com.drag.cstgroup.user.service.ScoreService;
import com.drag.cstgroup.user.vo.ScoreRecordVo;


@RestController
@RequestMapping(value = "/cstgroup/score", produces = "application/json;charset=utf-8")
public class ScoreController {
	
	private final static Logger log = LoggerFactory.getLogger(ScoreController.class);

	@Autowired
	private ScoreService scoreService;
	
	
	/**
	 * 查询积分记录
	 * @return
	 */
	@RequestMapping(value = "/scorerecord", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<ScoreRecordVo>> queryScore(@RequestParam String openid) {
		List<ScoreRecordVo> br = scoreService.queryScore(openid);
		return new ResponseEntity<List<ScoreRecordVo>>(br, HttpStatus.OK);
	}
	
	/**
	 * 赠送积分
	 * @param openid
	 * @param sendOpenid
	 * @param score
	 * @return
	 */
	@RequestMapping(value = "/scoresend", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<BaseResponse> scoreSend(@RequestParam String openid,@RequestParam String sendopenid,@RequestParam int score) {
		BaseResponse resp =  scoreService.sendScore(openid,sendopenid,score);
		return new ResponseEntity<BaseResponse>(resp, HttpStatus.OK);
	}
	
	/**
	 * 购买积分
	 * @param openid
	 * @param score
	 * @param price
	 * @return
	 */
	@RequestMapping(value = "/scorebuy", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<BaseResponse> buyScore(@RequestParam String openid,@RequestParam int score,@RequestParam BigDecimal price) {
		BaseResponse resp =  scoreService.buyScore(openid,score,price);
		return new ResponseEntity<BaseResponse>(resp, HttpStatus.OK);
	}
	
	
}
