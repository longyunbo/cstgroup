package com.drag.cstgroup.user.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserVo implements Serializable {

	private static final long serialVersionUID = -2779393742361373652L;
	/**
	 * id
	 */
	private int id;
	/**
	 * openid
	 */
	private String openid;
	/**
	 * 上级编号
	 */
	private String parentid;
	/**
	 * 用户类型：0-个人，1-企业
	 */
	private int type;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 性别0-男，1-女
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
	private String createTime;
	/**
	 * 更新时间
	 */
	private String updateTime;
	
}
