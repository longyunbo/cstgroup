package com.drag.cstgroup.user.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class UserVo implements Serializable {

	private static final long serialVersionUID = -2779393742361373652L;
	/**
	 * id
	 */
	@Id
	private int id;
	/**
	 * openid
	 */
	private String openid;
	/**
	 * 头像
	 */
	private String avatar;
	/**
	 * 性别
	 */
//	private int sex;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 真实姓名
	 */
//	private String realname;
	/**
	 * 年龄
	 */
//	private int age;
	/**
	 * 会员等级
	 */
	private int rankLevel;
	/**
	 * 恐龙骨
	 */
	private int dragBone;
	/**
	 * 经验值
	 */
	private int exp;
	/**
	 * 积分
	 */
	private String score;
	/**
	 * 砍价价格（砍价用到的字段）
	 */
	private BigDecimal price;
	/**
	 * 购买数量(秒杀用到的字段)
	 */
	private int number;
	/**
	 * 拼团，砍价，助力编号
	 */
	private String code;
}
