package com.drag.cstgroup.scoremall.form;

import java.util.List;

import lombok.Data;

/**
 * 订单form
 * @author longyunbo
 *
 */
@Data
public class OrderInfoForm {
	/**
	 * 用户id
	 */
	private String openid;
	/**
	 * 商品类型,yj-有机食品,ly-旅游服务,qy-企业服务,tc-营养套餐
	 */
	private String type;
	/**
	 * 总数量
	 */
	private int number;
	/**
	 * 总积分
	 */
	private int score;
	/**
	 * 开票类型
	 */
	private String billingType;
	/**
	 * 发票抬头
	 */
	private String invPayee;
	/**
	 * 发票内容
	 */
	private String invContent;
	/**
	 * 订单方式（0:快递到家，1:送货上门）
	 */
	private String orderType;
	/**
	 * 收货人
	 */
	private String receiptName;
	/**
	 * 收货人联系方式
	 */
	private String receiptTel;
	/**
	 * 所在区域
	 */
	private String region;
	/**
	 * 邮政编码
	 */
	private String postalcode;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * formId
	 */
	private String formId;
	/**
	 * 订单详情
	 */
	List<OrderDetailForm> orderDetail;
}
