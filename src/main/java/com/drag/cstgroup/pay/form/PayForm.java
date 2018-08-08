package com.drag.cstgroup.pay.form;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PayForm {
	
	//用户编号
	private String openid;
	
	//金额 元
	private BigDecimal price;
	
	//微信支付商户单号
	private String outTradeNo;
	
}
