package com.drag.cstgroup.common;


public final class Constant {
	//成功
	public static final String SUCCESS = "0000";
	//失败
	public static final String FAIL = "9999";
	public final static String fomat = "yyyy-MM-dd HH:mm:ss";
	public final static String fomatNo = "yyyy-MM-dd";
	public final static int size = 2000;
	/**
	 * 活动类型
	 */
	public final static String TYPE_PT = "pt";
	public final static String TYPE_MS = "ms";
	public final static String TYPE_KJ = "kj";
	public final static String TYPE_ZL = "zl";
	public final static String TYPE_DR = "drag";
	
	//消息发送状态
	public final static int SENDSTATUS_SUCC = 1;
	public final static int SENDSTATUS_FAIL = 2;

	//用户不存在
	public static final String USERNOTEXISTS = "9001";
	//活动不存在
	public static final String ACTIVITYNOTEXISTS = "9002";
	//活动已结束
	public static final String ACTIVITYALREADYEND_FAIL = "9003";
	//活动已完成
	public static final String ACTIVITYALREADYDOWN_FAIL = "9004";
	//用户已参加活动
	public static final String USERALREADYIN_FAIL = "9005";
	//商品不存在
	public static final String PRODUCTNOTEXISTS = "9006";
	//商品库存不足
	public static final String STOCK_FAIL = "9007";
	//卡券不存在
	public static final String TICKETNOTEXISTS = "9008";
	//卡券已经被核销
	public static final String TICKET_DESTORY = "9009";
	//卡券已经过期
	public static final String TICKET_OVER = "9010";
	//用户权限不够
	public static final String AUTH_OVER = "9011";
	
	
}
