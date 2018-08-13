package com.drag.cstgroup.user.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.keruyun.service.KeruyunService;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserProfessionDao;
import com.drag.cstgroup.user.dao.UserRankLevelDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserProfession;
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
	@Autowired
	private UserProfessionDao userProfessionDao;
	@Autowired
	private KeruyunService keruyunService;

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
			int uid = user.getId();
			user.setId(uid);
			user.setCreateTime(new Timestamp(System.currentTimeMillis()));
			user.setRankLevel(0);
			user.setBalance(BigDecimal.ZERO);
			user.setScore(0);
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
	 * 完善用户
	 * @param form
	 * @return
	 */
	@Transactional
	public UserResp createCustomer(UserForm form) {
		log.info("【完善用户传入参数】form = {}",JSON.toJSONString(form));
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
			//调用客如云创建用户方法
			UserResp resp = keruyunService.createCustomer(form);
			String returnCode = resp.getReturnCode();
			if(returnCode.equals(Constant.SUCCESS)) {
				us.setCustomerId(resp.getCustomerId());
				us.setCustomerMainId(resp.getCustomerMainId());
				userDao.saveAndFlush(us);
				baseResp.setReturnCode(Constant.SUCCESS);
				baseResp.setErrorMessage("更新用户成功!");
				return baseResp;
			}else {
				baseResp.setReturnCode(Constant.FAIL);
				baseResp.setErrorMessage(resp.getErrorMessage());
				return baseResp;
			}
		} catch (Exception e) {
			log.error("【更新用户信息异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
	}
	
	/**
	 * 更新用户接口
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
			return baseResp;
		} catch (Exception e) {
			log.error("【更新用户信息异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
	}
	
	/**
	 * 根据openid获取用户信息
	 * @param openid
	 * @return
	 */
	public UserVo queryUserByOpenid(String openid) {
		log.info("【根据openid获取用户信息】openid = {}",openid);
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
	
	/**
	 *  根据parentid获取用户信息
	 * @param parentid
	 * @return
	 */
	public UserVo queryUserByParent(int parentid) {
		log.info("【根据parentid获取用户信息】parentid = {}",parentid);
		UserVo userVo = new UserVo();
		try {
			User user = userDao.findById(parentid);
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
	
	
	/**
	 * 根据openid获取用户下级信息
	 * @param openid
	 * @return
	 */
	public List<UserVo> queryChidrenUser(int uid) {
		log.info("【根据uid获取用户下级信息】uid = {}",uid);
		List<UserVo> userList = new ArrayList<UserVo>();
		try {
			List<User> users = userDao.findByParentId(uid);
			if(users != null && users.size() > 0) {
				for(User us : users) {
					UserVo vo = new UserVo();
					BeanUtils.copyProperties(us, vo,new String[]{"createTime", "updateTime"});
					vo.setCreateTime((DateUtil.format(us.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
					vo.setUpdateTime((DateUtil.format(us.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
					userList.add(vo);
				}
			}
		} catch (Exception e) {
			log.error("获取用户异常,{}",e);
		}
		return userList;
	}
	
	/**
	 * 查询职业
	 * @return
	 */
	public List<UserProfession> queryProfession() {
		List<UserProfession> resp = userProfessionDao.findAll();
		return resp;
	}
	
	
	
	
	
}

