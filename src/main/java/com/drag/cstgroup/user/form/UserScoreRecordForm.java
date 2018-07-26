package com.drag.cstgroup.user.form;

import lombok.Data;

@Data
public class UserScoreRecordForm {
	
	/**
	 * 用户id
	 */
	private String uid;
	/**
	 * 商品编号
	 */
	private int goodsId;
	/**
	 * 类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,drag-积分
	 */
	private String type;
	/**
	 * 当前积分
	 */
	private int score;
	/**
	 * 获得积分
	 */
	private int availablescore;
	
	
	
}
