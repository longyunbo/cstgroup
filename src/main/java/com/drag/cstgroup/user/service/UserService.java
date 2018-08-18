package com.drag.cstgroup.user.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.keruyun.service.KeruyunService;
import com.drag.cstgroup.scoremall.form.OrderInfoForm;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserProfessionDao;
import com.drag.cstgroup.user.dao.UserRankLevelDao;
import com.drag.cstgroup.user.dao.UserReceivingAddressDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserProfession;
import com.drag.cstgroup.user.entity.UserRankLevel;
import com.drag.cstgroup.user.entity.UserReceivingAddress;
import com.drag.cstgroup.user.form.UserForm;
import com.drag.cstgroup.user.resp.UserResp;
import com.drag.cstgroup.user.vo.UserReceivingAddressVo;
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
	@Autowired
	private UserReceivingAddressDao userReceivingAddressDao;

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
	 * 更新用户地址接口
	 * @param form
	 * @return
	 */
	@Transactional
	public UserResp userAddress(OrderInfoForm form) {
		log.info("【更新地址传入参数】form = {}",JSON.toJSONString(form));
		UserResp baseResp = new UserResp();
		try {
			String openid = form.getOpenid();
			User us = userDao.findByOpenid(openid);
			if(us == null) {
				baseResp.setReturnCode(Constant.FAIL);
				baseResp.setErrorMessage("该用户不存在!");
				return baseResp;
			}
			int uid = us.getId();
			//收货人
			String receiptName  = form.getReceiptName();
			//收货人联系方式
			String receiptTel = form.getReceiptTel();
			//所在区域
			String region = form.getRegion();
			//邮政编码
			String postalcode = form.getPostalcode();
			//地址
			String receiptAddress = form.getReceiptAddress();
			UserReceivingAddress address = userReceivingAddressDao.findByUid(uid);
			if(address != null) {
				BeanUtils.copyProperties(form, address);
				address.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				userReceivingAddressDao.saveAndFlush(address);
			}else {
				address = new UserReceivingAddress();
				address.setId(address.getId());
				address.setPostalcode(postalcode);
				address.setReceiptAddress(receiptAddress);
				address.setReceiptName(receiptName);
				address.setReceiptTel(receiptTel);
				address.setRegion(region);
				address.setUid(uid);
				address.setCreateTime(new Timestamp(System.currentTimeMillis()));
				address.setUpdateTime(new Timestamp(System.currentTimeMillis()));
				userReceivingAddressDao.save(address);
			}
			baseResp.setReturnCode(Constant.SUCCESS);
			baseResp.setErrorMessage("更新地址成功!");
			return baseResp;
		} catch (Exception e) {
			log.error("【更新用户地址信息异常】{}",e);
			throw AMPException.getException("系统异常!");
		}
	}
	
	/**
	 * 查询地址信息
	 * @param openid
	 * @return
	 */
	public UserReceivingAddressVo queryAddressByOpenid(String openid) {
		log.info("【根据openid获取用户地址信息】openid = {}",openid);
		UserReceivingAddressVo addressVo = new UserReceivingAddressVo();
		try {
			User user = userDao.findByOpenid(openid);
			if(user != null) {
				int uid = user.getId();
				UserReceivingAddress address = userReceivingAddressDao.findByUid(uid);
				if(address != null) {
					BeanUtils.copyProperties(address, addressVo,new String[]{"createTime", "updateTime"});
					addressVo.setCreateTime((DateUtil.format(user.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
					addressVo.setUpdateTime((DateUtil.format(user.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
				}
				
			}
		} catch (Exception e) {
			log.error("获取用户地址异常,{}",e);
		}
		return addressVo;
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
	
	
	/**
	 * 用户上传图片
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
	        //获取文件需要上传到的路径
	        File directory = new File("../cstgroup");
	        String path = directory.getCanonicalPath() + "/upload/";
	        String companyname = request.getParameter("companyname").toString();
	        // 判断存放上传文件的目录是否存在（不存在则创建）
	        File dir = new File(path);
	        if (!dir.exists()) {
	            dir.mkdir();
	        }
	        log.debug("path=" + path);
	        request.setCharacterEncoding("utf-8"); //设置编码
	        JSONArray jsonArray = new JSONArray();
	        try {
	            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
	            Iterator<String> iterator = req.getFileNames();
	            while (iterator.hasNext()) {
	                HashMap<String, Object> res = new HashMap<String, Object>();
	                MultipartFile file = req.getFile(iterator.next());
	                // 获取文件名
	                String fileNames = file.getOriginalFilename();
	                int split = fileNames.lastIndexOf(".");
	                //获取上传文件的后缀
	                String extName = fileNames.substring(split + 1, fileNames.length());
	                //组成新的图片名称
	                String newName = companyname + "." + extName;
	                String destPath = path + newName;
	                log.debug("destPath=" + destPath);
	 
	                //真正写到磁盘上
	                File file1 = new File(destPath);
	                OutputStream out = new FileOutputStream(file1);
	                out.write(file.getBytes());
	                res.put("url", destPath);
	                jsonArray.add(res);
	 
	                out.close();
	            }
	        } catch (Exception e) {
	            log.error("", e);
	        }
	 
	        PrintWriter printWriter = response.getWriter();
	        response.setContentType("application/json");
	        response.setCharacterEncoding("utf-8");
	        printWriter.write(JSON.toJSONString(jsonArray));
	        printWriter.flush();
	 
	    }
	
	
	
}

