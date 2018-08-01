package com.drag.cstgroup.scoremall.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 商品信息表
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_order_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class OrderInfo implements Serializable {

	private static final long serialVersionUID = 8976480717545183113L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	 * 用户编号
	 */
	private String openid;
	/**
	 * 买家姓名
	 */
	private int buyName;
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
	private BigDecimal score;
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
	private String deliverystatus;
	/**
	 * 物流公司名称
	 */
	private String compname;
	/**
	 * 商品详细描述
	 */
	private String content;
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
	 * 是否结束，1，是；0，否
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
	 * 确认收货时间
	 */
	private Date confirmReceiptTime;
	/**
	 * 退款单号
	 */
	private String refundcode;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
