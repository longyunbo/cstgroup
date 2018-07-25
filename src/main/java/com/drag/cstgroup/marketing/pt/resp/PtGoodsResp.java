package com.drag.cstgroup.marketing.pt.resp;

import com.drag.cstgroup.common.BaseResponse;

import lombok.Data;

@Data
public class PtGoodsResp extends BaseResponse{
	
	private static final long serialVersionUID = -4195525113654121659L;
	
	private int ptgoodsId;
	
	private String ptcode;
}
