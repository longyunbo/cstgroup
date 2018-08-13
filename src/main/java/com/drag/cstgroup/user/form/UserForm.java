package com.drag.cstgroup.user.form;

import lombok.Data;

@Data
public class UserForm {
	
	/**
	 * openid
	 */
	private String openid;
	/**
	 * parentid
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
	 * 昵称
	 */
	private String nickname;
	/**
	 * 姓名
	 */
	private String realname;
	/**
	 * 性别：0-女，1-男
	 */
	private int sex;
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
}
