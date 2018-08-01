package com.drag.cstgroup.scoremall.controller;

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

import com.drag.cstgroup.scoremall.service.ProductInfoService;
import com.drag.cstgroup.scoremall.vo.ProductInfoVo;


@RestController
@RequestMapping(value = "/cstgroup/product", produces = "application/json;charset=utf-8")
public class ProductInfoController {
	
	private final static Logger log = LoggerFactory.getLogger(ProductInfoController.class);

	@Autowired
	private ProductInfoService productInfoService;
	
	/**
	 * 根据类型查询查询商品
	 * @return
	 */
	@RequestMapping(value = "/listgoods", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<ProductInfoVo>> listGoods(@RequestParam(required = true) String type) {
		List<ProductInfoVo> rows= productInfoService.listGoods(type);
		return new ResponseEntity<List<ProductInfoVo>>(rows, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/detail", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<ProductInfoVo> detail(@RequestParam(required = true) int goodsId) {
		ProductInfoVo detailVo = productInfoService.goodsDetail(goodsId);
		return new ResponseEntity<ProductInfoVo>(detailVo, HttpStatus.OK);
	}
	
	
	
}
