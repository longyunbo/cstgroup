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
 * 发货地址
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_order_shipper")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class OrderShipper implements Serializable {

	private static final long serialVersionUID = -1104904357333122373L;
	/**
	 * 自增id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 订单编号
	 */
	private String orderid;
	/**
	 * 收货时间
	 */
	private Date recipientTime;
	/**
	 * 联系人
	 */
//	private String linkman;
	/**
	 * 联系人号码
	 */
//	private String linkmanPhone;
	/**
	 * 发货时间
	 */
	private Date shipperTime;
	/**
	 * 发货人手机号
	 */
	private String shipperTel;
	/**
	 * 发货状态，1：已发货，2：已收货，3：已拒绝
	 */
	private String status;
	/**
	 * 发货地址
	 */
	private String shipperAddress;
	/**
	 * 收货人
	 */
	private String receiptName;
	/**
	 * 收货人联系方式
	 */
	private String receiptTel;
	/**
	 * 收货信息地址
	 */
	private String receiptAddress;
	/**
	 * 拒绝原因
	 */
	private String rejectReason;
	/**
	 * 物流公司名称
	 */
	private String compname;
	/**
	 * 物流单号
	 */
	private String transportId;
	/**
	 * 物流费用
	 */
	private BigDecimal transportCost;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
