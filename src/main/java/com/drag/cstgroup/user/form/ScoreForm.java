package com.drag.cstgroup.user.form;

import lombok.Data;

@Data
public class ScoreForm {
	
	/**
	 * 用户id
	 */
	private String openid;
	/**
	 * 商品编号
	 */
	private int goodsId;
	/**
	 * 类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,drag-积分
	 */
	private String type;
	/**
	 * 消耗积分
	 */
	private int score;
	
	
	
}
