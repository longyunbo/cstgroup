package com.drag.cstgroup.user.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

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
	
	
	
	public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取文件需要上传到的路径
        String path = request.getRealPath("/upload") + "/";
        
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        log.debug("path=" + path);

        request.setCharacterEncoding("utf-8");  //设置编码
        //获得磁盘文件条目工厂
        DiskFileItemFactory factory = new DiskFileItemFactory();

        //如果没以下两行设置的话,上传大的文件会占用很多内存，
        //设置暂时存放的存储室,这个存储室可以和最终存储文件的目录不同
        /**
         * 原理: 它是先存到暂时存储室，然后再真正写到对应目录的硬盘上，
         * 按理来说当上传一个文件时，其实是上传了两份，第一个是以 .tem 格式的
         * 然后再将其真正写到对应目录的硬盘上
         */
        factory.setRepository(dir);
        //设置缓存的大小，当上传文件的容量超过该缓存时，直接放到暂时存储室
        factory.setSizeThreshold(1024 * 1024);
        //高水平的API文件上传处理
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            List<FileItem> list = upload.parseRequest(request);
            FileItem picture = null;
            for (FileItem item : list) {
                //获取表单的属性名字
                String name = item.getFieldName();
                //如果获取的表单信息是普通的 文本 信息
                if (item.isFormField()) {
                    //获取用户具体输入的字符串
                    String value = item.getString();
                    request.setAttribute(name, value);
                    log.debug("name=" + name + ",value=" + value);
                } else {
                    picture = item;
                }
            }

            //自定义上传图片的名字为userId.jpg
            String fileName = request.getAttribute("userId") + ".jpg";
            String destPath = path + fileName;
            log.debug("destPath=" + destPath);

            //真正写到磁盘上
            File file = new File(destPath);
            OutputStream out = new FileOutputStream(file);
            InputStream in = picture.getInputStream();
            int length = 0;
            byte[] buf = new byte[1024];
            // in.read(buf) 每次读到的数据存放在buf 数组中
            while ((length = in.read(buf)) != -1) {
                //在buf数组中取出数据写到（输出流）磁盘上
                out.write(buf, 0, length);
            }
            in.close();
            out.close();
        } catch (FileUploadException e1) {
            log.error("", e1);
        } catch (Exception e) {
            log.error("", e);
        }


        PrintWriter printWriter = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        HashMap<String, Object> res = new HashMap<String, Object>();
        res.put("success", true);
        printWriter.write(JSON.toJSONString(res));
        printWriter.flush();
    }
	
	
	
	
}

