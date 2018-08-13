package com.drag.cstgroup.pay.resp;

import com.drag.cstgroup.common.BaseResponse;

import lombok.Data;

@Data
public class PayResp extends BaseResponse{
	
	private static final long serialVersionUID = -2350517572294913622L;

	private String balance;

}
