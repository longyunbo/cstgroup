package com.drag.cstgroup.user.resp;

import com.drag.cstgroup.common.BaseResponse;

import lombok.Data;

@Data
public class ScoreResp extends BaseResponse {

	private static final long serialVersionUID = -2207917189393373444L;

	/**
	 * 会员当前积分
	 */
	private String currentPoints;
}
