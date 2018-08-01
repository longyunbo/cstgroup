package com.drag.cstgroup.scoremall.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
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
 * 收货地址
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_receiving_address")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ReceivingAddress implements Serializable {

	private static final long serialVersionUID = -1104904357333122373L;
	/**
	 * 自增id
	 */
	@Id
	private int id;
	/**
	 * 用户编号
	 */
	private String openid;
	/**
	 * 收货人
	 */
	private String receiver;
	/**
	 * 联系方式
	 */
	private String phone;
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
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date updateTime;

}
