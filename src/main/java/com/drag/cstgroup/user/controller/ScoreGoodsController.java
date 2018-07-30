package com.drag.cstgroup.user.controller;

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

import com.drag.cstgroup.common.BaseResponse;
import com.drag.cstgroup.user.form.ScoreForm;
import com.drag.cstgroup.user.service.ScoreGoodsService;
import com.drag.cstgroup.user.vo.ScoreGoodsVo;
import com.drag.cstgroup.user.vo.UserScoreUsedRecordVo;
import com.drag.cstgroup.user.vo.UserTicketTemplateVo;




@RestController
@RequestMapping(value = "/chirouosopark/draggoods")
public class ScoreGoodsController {
	
	private final static Logger log = LoggerFactory.getLogger(ScoreGoodsController.class);

	@Autowired
	private ScoreGoodsService drGoodsService;
	
	/**
	 * 查询所有的积分兑换商品
	 * @return
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<ScoreGoodsVo>> listGoods() {
		List<ScoreGoodsVo> rows= drGoodsService.listGoods();
		return new ResponseEntity<List<ScoreGoodsVo>>(rows, HttpStatus.OK);
	}
	
	/**
	 * 查询积分兑换商品详情
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/detail", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserTicketTemplateVo> detail(@RequestParam(required = true) int goodsId) {
		UserTicketTemplateVo detailVo = drGoodsService.goodsDetail(goodsId);
		return new ResponseEntity<UserTicketTemplateVo>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 积分立即兑换
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/exchange", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<BaseResponse> exchange(@RequestBody ScoreForm form) {
		BaseResponse br = drGoodsService.exchange(form);
		return new ResponseEntity<BaseResponse>(br, HttpStatus.OK);
	}
	
	/**
	 * 查询积分兑换记录
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/listrecord", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<UserScoreUsedRecordVo>> listRecord(@RequestParam(required = true) String openid) {
		List<UserScoreUsedRecordVo> rows= drGoodsService.listRecord(openid);
		return new ResponseEntity<List<UserScoreUsedRecordVo>>(rows, HttpStatus.OK);
	}
	
	/**
	 * 查询积分详情
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/listallrecord", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<UserScoreUsedRecordVo>> listAllRecord(@RequestParam(required = true) String openid) {
		List<UserScoreUsedRecordVo> rows= drGoodsService.listAllRecord(openid);
		return new ResponseEntity<List<UserScoreUsedRecordVo>>(rows, HttpStatus.OK);
	}
	
}
