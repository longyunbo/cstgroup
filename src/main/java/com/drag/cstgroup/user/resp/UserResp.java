package com.drag.cstgroup.user.resp;

import com.drag.cstgroup.common.BaseResponse;

import lombok.Data;

@Data
public class UserResp extends BaseResponse{
	
	private static final long serialVersionUID = 124623679891285568L;
	//客如云客户编号
	private String customerId;
	
	private String customerMainId;

}
