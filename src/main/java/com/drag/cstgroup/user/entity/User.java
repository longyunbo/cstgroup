package com.drag.cstgroup.user.entity;

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
@Table(name = "t_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class User implements Serializable {

	private static final long serialVersionUID = 8262803439444253531L;
	//企业审核状态:0-审核中，1-审核通过，2-审核不通过
	public final static int CHECKSTATUS_MID = 0;
	public final static int CHECKSTATUS_YES = 1;
	public final static int CHECKSTATUS_NO = 2;
	//性别
	public final static int SEX_MALE = 0;
	public final static int SEX_FEMALE = 1;
	
	
	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/**
	 * openid
	 */
	private String openid;
	/**
	 * 上级编号
	 */
	private int parentid;
	/**
	 * 用户类型：0-个人，1-企业
	 */
	private int type;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 性别0-女，1-男
	 */
	private int sex;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 真实姓名
	 */
	private String realname;
	/**
	 * 客如云顾客ID
	 */
	private String customerId;
	/**
	 * 客如云顾客主ID
	 */
	private String customerMainId;
	/**
	 * 生日
	 */
	private String birthday;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 手机
	 */
	private String mobile;
	/**
	 * 职业
	 */
	private String profession;
	/**
	 * 职位
	 */
	private String job;
	/**
	 * 会员等级
	 */
	private int rankLevel;
	/**
	 * 积分
	 */
	private int score;
	/**
	 * 余额
	 */
	private BigDecimal balance;
	/**
	 * 赠送余额
	 */
	private BigDecimal extraBalance;
	/**
	 * 总充值金额
	 */
	private int rechargeBalance;
	
	//-----------企业信息------------
	/**
	 * 企业名称
	 */
	private String companyName;
	/**
	 * 企业税号
	 */
	private String dutyParagraph;
	/**
	 * 企业地址
	 */
	private String companyAddress;
	/**
	 * 企业照片
	 */
	private String companyImg;
	/**
	 * 企业审核状态
	 */
	private int checkStatus;
	//-----------企业信息------------
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
