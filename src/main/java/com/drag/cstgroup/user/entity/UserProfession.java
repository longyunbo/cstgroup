package com.drag.cstgroup.user.entity;

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
 * 职业表
 * @author longyunbo
 *
 */
@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "t_user_profession")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class UserProfession implements Serializable {

	private static final long serialVersionUID = -1397345119081076644L;
	/**
	 * id
	 */
	@Id
	private int id;
	/**
	 * 职业类型
	 */
	private String professionType;
	/**
	 * 职业名称
	 */
	private String professionName;
	/**
	 * 创建时间
	 */
	private Date createTime;
}
