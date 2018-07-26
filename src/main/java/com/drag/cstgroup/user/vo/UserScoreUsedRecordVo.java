package com.drag.cstgroup.user.vo;

import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 会员积分使用记录
 * @author longyunbo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class UserScoreUsedRecordVo{

	@Id
	private int id;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 商品编号
	 */
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,drag-积分
	 */
	private String type;
	/**
	 * 当前积分
	 */
	private int score;
	/**
	 * 使用积分
	 */
	private int usedscore;
	/**
	 * 创建时间
	 */
	private String createTime;

}
