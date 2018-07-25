package com.drag.cstgroup.marketing.kj.resp;

import java.math.BigDecimal;

import com.drag.cstgroup.common.BaseResponse;

import lombok.Data;

@Data
public class KjGoodsResp extends BaseResponse{
	
	private static final long serialVersionUID = -4761015373320632665L;

	private int kjgoodsId;
	
	private String kjcode;
	
	private BigDecimal price;
}
