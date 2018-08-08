package com.drag.cstgroup.user.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * 积分记录
 * @author longyunbo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ScoreRecordVo{

	private int id;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 对方编号
	 */
	private int fuid;
	/**
	 * 商品编号
	 */
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * return-下级返回积分,recharge-充值,
	 * yj-有机食品,ly-旅游服务,qy-企业服务,tc-营养套餐，send-赠送积分
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
