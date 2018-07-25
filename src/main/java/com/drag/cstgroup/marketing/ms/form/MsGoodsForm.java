package com.drag.cstgroup.marketing.ms.form;

import lombok.Data;

@Data
public class MsGoodsForm {
	
	/**
	 * 秒杀商品id
	 */
	private int msgoodsId;
	/**
	 * 用户id
	 */
	private String openid;
	/**
	 * 退款单编号
	 */
//	private String msrefundcode;
	/**
	 * 商品名称
	 */
//	private String msgoodsName;
	/**
	 * 单价
	 */
//	private BigDecimal perPrice;
	/**
	 * 付款金额
	 */
//	private BigDecimal price;
	/**
	 * 数量
	 */
	private int number;
	
}
