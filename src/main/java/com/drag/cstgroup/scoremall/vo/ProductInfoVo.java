package com.drag.cstgroup.scoremall.vo;

import java.math.BigDecimal;

import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品
 * @author longyunbo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class ProductInfoVo {
	/**
	 * 商品自增id
	 */
	@Id
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 商品类型,yj-有机食品,ly-旅游服务,qy-企业服务,tc-营养套餐
	 */
	private String type;
	/**
	 * 购买规格
	 */
	private int norms;
	/**
	 * 消耗积分
	 */
	private BigDecimal score;
	/**
	 * 开始时间
	 */
	private String startTime;
	/**
	 * 结束时间
	 */
	private String endTime;
	/**
	 * 商品库存数量
	 */
	private int goodsNumber;
	/**
	 * 商品剪短描述
	 */
	private String description;
	/**
	 * 商品详细描述
	 */
	private String content;
	/**
	 * 经验值
	 */
	private int exp;
	/**
	 * 商品微缩图
	 */
	private String goodsThumb;
	/**
	 * 商品详情轮播图
	 */
	private String goodsImgs;
	/**
	 * 该商品显示顺序（越大越靠后）
	 */
	private int sort;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 修改时间
	 */
	private String updateTime;
	/**
	 * 是否结束，1，是；0，否
	 */
	private int isEnd;
	/**
	 * 完成人数
	 */
	private int succTimes;

}
