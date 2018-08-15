package com.drag.cstgroup.scoremall.vo;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商品信息
 * @author longyunbo
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class OrderInfoVo implements Serializable {
	
	private static final long serialVersionUID = 4674677983620486166L;
	private int id;
	/**
	 * 订单编号
	 */
	private String orderid;
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
	 * 商品图片
	 */
	private String goodsImg;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 买家姓名
	 */
	private String buyName;
	/**
	 * 买家电话
	 */
	private String phone;
	/**
	 * 商品总数量
	 */
	private int number;
	/**
	 * 消耗积分
	 */
	private int score;
	/**
	 * 购买规格
	 */
	private String norms;
	/**
	 * 订单状态，0:已付款,  1:已退款
	 */
	private int orderstatus;
	/**
	 * 订单方式（0:快递到家，1:送货上门）
	 */
	private int orderType;
	/**
	 * 发货状态，0：未发货，1：已发货，2：已收货，3：已拒绝
	 */
	private int deliverystatus;
	/**
	 * 物流公司名称
	 */
	private String compname;
//	/**
//	 * 商品详细描述
//	 */
//	private String content;
	/**
	 * 物流单号
	 */
	private String transportId;
	/**
	 * 收货人姓名
	 */
	private String receiptName;
	/**
	 * 收货人电话
	 */
	private String receiptTel;
	/**
	 * 收货区域信息
	 */
	private String region;
	/**
	 * 邮政编码
	 */
	private String postalcode;
	/**
	 * 收货信息地址
	 */
	private String receiptAddress;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 是否开票，0-否，1-是
	 */
	private int isBilling;
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
	 * 开票时间
	 */
	private String billTime;
	/**
	 * 确认收货时间
	 */
	private String confirmReceiptTime;
	/**
	 * 退款单号
	 */
	private String refundcode;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 修改时间
	 */
	private String updateTime;

}
