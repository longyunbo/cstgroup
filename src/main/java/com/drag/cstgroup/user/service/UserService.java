package com.drag.cstgroup.user.service;

import java.sql.Timestamp;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserRankLevelDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserRankLevel;
import com.drag.cstgroup.user.form.UserForm;
import com.drag.cstgroup.user.resp.UserResp;
import com.drag.cstgroup.user.vo.UserVo;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserRankLevelDao userRankLevelDao;

	/**
	 * 检查权限
	 * @return
	 */
	public Boolean checkAuth(User user,String authIds) {
		boolean authFlag = false;
		try {
			if(user != null) {
				int rankLevel = user.getRankLevel();
				UserRankLevel userRankLevel  = userRankLevelDao.findByLevel(rankLevel);
				String auth = userRankLevel.getAuth();
				if(auth.contains(authIds)) {
					authFlag = true;
				}else {
					authFlag = false;
				}
			}
		} catch (Exception e) {
			log.error("检查权限异常,{}",e);
		}
		return authFlag;
	}
    
	
	/**
	 * 新增用户信息
	 * @param form
	 * @return
	 */
	@Transactional
	public UserResp userAdd(UserForm form) {
		log.info("【新增用户传入参数】form = {}",JSON.toJSONString(form));
		UserResp baseResp = new UserResp();
		try {
			User user = new User();
			String openid = form.getOpenid();
			User us = userDao.findByOpenid(openid);
			if(us != null) {
				baseResp.setReturnCode(Constant.FAIL);
				baseResp.setErrorMessage("该用户已存在!");
				return baseResp;
			}
			int type  = form.getType();
			//如果为个人用户，不用审核，默认为审核通过，企业用户为审核中
			if(type == 0) {
				user.setCheckStatus(User.CHECKSTATUS_YES);
			}else {
				user.setCheckStatus(User.CHECKSTATUS_MID);
			}
			BeanUtils.copyProperties(form, user,new String[]{"createTime", "updateTime"});
			user.setCreateTime(new Timestamp(System.currentTimeMillis()));
			user.setRankLevel(0);
			userDao.save(user);
			baseResp.setReturnCode(Constant.SUCCESS);
			baseResp.setErrorMessage("新增用户成功!");
		} catch (Exception e) {
			log.error("【新增用户信息异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		return baseResp;
	}
	
	/**
	 * 更新用户
	 * @param form
	 * @return
	 */
	@Transactional
	public UserResp userUpdate(UserForm form) {
		log.info("【更新用户传入参数】form = {}",JSON.toJSONString(form));
		UserResp baseResp = new UserResp();
		try {
			String openid = form.getOpenid();
			User us = userDao.findByOpenid(openid);
			if(us == null) {
				baseResp.setReturnCode(Constant.FAIL);
				baseResp.setErrorMessage("该用户不存在!");
				return baseResp;
			}
			BeanUtils.copyProperties(form, us);
			us.setUpdateTime(new Timestamp(System.currentTimeMillis()));
			userDao.saveAndFlush(us);
			baseResp.setReturnCode(Constant.SUCCESS);
			baseResp.setErrorMessage("更新用户成功!");
		} catch (Exception e) {
			log.error("【更新用户信息异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
		return baseResp;
	}
	
	/**
	 * 根据openid获取用户信息
	 * @param openid
	 * @return
	 */
	public UserVo queryUserByOpenid(String openid) {
		UserVo userVo = new UserVo();
		try {
			User user = userDao.findByOpenid(openid);
			if(user != null) {
				BeanUtils.copyProperties(user, userVo,new String[]{"createTime", "updateTime"});
				userVo.setCreateTime((DateUtil.format(user.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				userVo.setUpdateTime((DateUtil.format(user.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
			}
		} catch (Exception e) {
			log.error("获取用户异常,{}",e);
		}
		return userVo;
	}
	
	
	
}
