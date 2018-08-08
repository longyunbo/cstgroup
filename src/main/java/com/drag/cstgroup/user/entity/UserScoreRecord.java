package com.drag.cstgroup.user.entity;

import java.io.Serializable;
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
 * 会员积分记录表
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_user_score_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserScoreRecord implements Serializable {

	private static final long serialVersionUID = -5807402303998048344L;
	
	//积分来源类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,send-赠送积分,return-下级返回积分,recharge-充值,buy-积分购买,get-获赠
	public static String TYPE_RECHARGE = "recharge";
	public static String TYPE_RETURN = "return";
	public static String TYPE_SEND = "send";
	public static String TYPE_GET = "get";
	public static String TYPE_BUY = "buy";
	
	/**
	 * id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	/**
	 * 用户编号
	 */
	private int uid;
	/**
	 * 赠送者、下级返回者编号
	 */
	private int fuid;
	/**
	 * 商品编号
	 */
	private int goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,send-赠送积分,return-下级返回积分,recharge-充值
	 */
	private String type;
	/**
	 * 当前积分
	 */
	private int score;
	/**
	 * 获得积分
	 */
	private int availableScore;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
