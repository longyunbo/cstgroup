package com.drag.cstgroup.marketing.ms.controller;

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

import com.drag.cstgroup.marketing.ms.form.MsGoodsForm;
import com.drag.cstgroup.marketing.ms.resp.MsGoodsResp;
import com.drag.cstgroup.marketing.ms.service.MsGoodsService;
import com.drag.cstgroup.marketing.ms.vo.MsGoodsDetailVo;
import com.drag.cstgroup.marketing.ms.vo.MsGoodsVo;


@RestController
@RequestMapping(value = "/chirouosopark/msgoods", produces = "application/json;charset=utf-8")
public class MsGoodsController {
	
	private final static Logger log = LoggerFactory.getLogger(MsGoodsController.class);

	@Autowired
	private MsGoodsService msGoodsService;
	
	/**
	 * 查询所有的秒杀商品
	 * @return
	 */
	@RequestMapping(value = "/list", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<MsGoodsVo>> listGoods() {
		List<MsGoodsVo> rows= msGoodsService.listGoods();
		return new ResponseEntity<List<MsGoodsVo>>(rows, HttpStatus.OK);
	}
	
	/**
	 * 查询秒杀详情(查询所有发起秒杀的用户)
	 * @param goodsId
	 * @return
	 */
	@RequestMapping(value = "/detail", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<MsGoodsDetailVo> detail(@RequestParam(required = true) int goodsId) {
		MsGoodsDetailVo detailVo = msGoodsService.goodsDetail(goodsId);
		return new ResponseEntity<MsGoodsDetailVo>(detailVo, HttpStatus.OK);
	}
	
	/**
	 * 查询秒杀活动是否结束
	 * @return
	 */
	@RequestMapping(value = "/checkend", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<Boolean> checkEnd(@RequestParam(required = true) int goodsId) {
		Boolean endFlag = msGoodsService.checkEnd(goodsId);
		return new ResponseEntity<Boolean>(endFlag, HttpStatus.OK);
	}
	
	
	/**
	 * 发起秒杀
	 * @return
	 */
	@RequestMapping(value = "/collage", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<MsGoodsResp> collage(@RequestBody MsGoodsForm form) {
		MsGoodsResp br = msGoodsService.collage(form);
		return new ResponseEntity<MsGoodsResp>(br, HttpStatus.OK);
	}
	
	
}
