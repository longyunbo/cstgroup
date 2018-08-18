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
	 * 订单号
	 */
	private String orderid;
	/**
	 * 用户id
	 */
	private String openid;
	/**
	 * 商品类型,yj-有机食品,ly-旅游服务,qy-企业服务,tc-营养套餐
	 */
	private String type;
	/**
	 * 商品编号(有机食品不需要传，其他的类型需传)
	 */
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 总数量
	 */
	private int number;
	/**
	 * 总积分
	 */
	private int score;
	/**
	 * 规格
	 */
	private String norms;
	/**
	 * 买家姓名
	 */
	private String buyName;
	/**
	 * 买家手机号
	 */
	private String phone;
	/**
	 * 是否开票，0-否，1-是
	 */
	private int isBilling;
	/**
	 * 0-个人，1-公司
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
	 * 发票电话
	 */
	private String invTel;
	/**
	 * 发票人姓名
	 */
	private String invName;
	/**
	 * 发票邮箱
	 */
	private String invEmail;
	/**
	 * 订单方式（0:快递到家，1:送货上门）
	 */
	private int orderType;
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
	 * 市级
	 */
	private String cityName;
	/**
	 * 地址
	 */
	private String receiptAddress;
	/**
	 * formId
	 */
	private String formId;
	/**
	 * 订单详情
	 */
	List<OrderDetailForm> orderDetail;
}
