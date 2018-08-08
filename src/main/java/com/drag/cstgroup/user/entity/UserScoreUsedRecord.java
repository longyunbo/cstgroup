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
 * 会员积分使用记录表
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_user_score_used_record")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserScoreUsedRecord implements Serializable {

	// 积分来源类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,send-赠送积分,return-下级返回积分,recharge-充值,buy-积分购买
	public static String TYPE_RECHARGE = "recharge";
	public static String TYPE_RETURN = "return";
	public static String TYPE_SEND = "send";
	
	private static final long serialVersionUID = -8720698126498205667L;
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
	 * 对方用户编号
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
	 * 类型:pt-拼团,ms-秒杀,zl-助力,kj-砍价,drag-积分
	 */
	private String type;
	/**
	 * 当前积分
	 */
	private int score;
	/**
	 * 使用积分
	 */
	private int usedScore;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
