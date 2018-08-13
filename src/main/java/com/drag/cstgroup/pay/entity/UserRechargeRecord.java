package com.drag.cstgroup.pay.entity;

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

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_user_recharge_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserRechargeRecord implements Serializable {
	
	private static final long serialVersionUID = 1132709062379976201L;
	//充值状态0-成功，1-失败
	public final static int STATUS_SUCC = 0;
	public final static int STATUS_FAIL = 1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 当前余额
	 */
	private BigDecimal balance;
	/**
	 * 充值金额
	 */
	private BigDecimal rechargeBalance;
	/**
	 * 充值赠送金额
	 */
	private BigDecimal extraBalance;
	
	/**
	 * 商户订单号
	 */
	private String outTradeNo;
	/**
	 * 充值状态,0-成功，1-失败
	 */
	private int status;
	/**
	 * 创建时间
	 */
	private Date createTime;
	

}
