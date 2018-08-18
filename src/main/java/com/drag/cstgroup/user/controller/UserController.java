package com.drag.cstgroup.user.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.drag.cstgroup.scoremall.form.OrderInfoForm;
import com.drag.cstgroup.user.entity.UserProfession;
import com.drag.cstgroup.user.form.UserForm;
import com.drag.cstgroup.user.resp.UserResp;
import com.drag.cstgroup.user.service.UserService;
import com.drag.cstgroup.user.vo.UserReceivingAddressVo;
import com.drag.cstgroup.user.vo.UserVo;
import com.drag.cstgroup.utils.WxUtil;


@RestController
@RequestMapping(value = "/cstgroup/user")
public class UserController {
	
	private final static Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	/**
	 * 根据code获取openid
	 * @param code
	 * @return
	 */
	@RequestMapping(value = "/openid", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<String> getOpenid(@RequestParam(required = true) String code) {
		String openid = WxUtil.returnOpenid(code);
		return new ResponseEntity<String>(openid, HttpStatus.OK);
	}
	
	/**
	 * 根据openid获取用户信息
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/queryuser", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserVo> queryUserByOpenid(@RequestParam(required = true) String openid) {
		UserVo userVo = userService.queryUserByOpenid(openid);
		return new ResponseEntity<UserVo>(userVo, HttpStatus.OK);
	}
	
	/**
	 * 用户新增
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/useradd", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserResp> userAdd(@RequestBody UserForm form) {
		UserResp br = userService.userAdd(form);
		return new ResponseEntity<UserResp>(br, HttpStatus.OK);
	}
	
	/**
	 * 完善用户（需调用客如云创建用户接口）
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/usersupply", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserResp> userSupply(@RequestBody UserForm form) {
		UserResp br = userService.createCustomer(form);
		return new ResponseEntity<UserResp>(br, HttpStatus.OK);
	}
	
	/**
	 * 更新用户
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/userupdate", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserResp> userUpdate(@RequestBody UserForm form) {
		UserResp br = userService.userUpdate(form);
		return new ResponseEntity<UserResp>(br, HttpStatus.OK);
	}
	
	/**
	 * 查询职业信息
	 * @return
	 */
	@RequestMapping(value = "/getprofession", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<UserProfession>> queryProfession() {
		List<UserProfession> br = userService.queryProfession();
		return new ResponseEntity<List<UserProfession>>(br, HttpStatus.OK);
	}
	
	/**
	 * 查询父级用户
	 * @param parentid
	 * @return
	 */
	@RequestMapping(value = "/myparent", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserVo> queryUserByParent(@RequestParam(required = true) int parentid) {
		UserVo userVo = userService.queryUserByParent(parentid);
		return new ResponseEntity<UserVo>(userVo, HttpStatus.OK);
	}
	
	
	/**
	 * 根据openid获取用户下级信息
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/mychidren", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<List<UserVo>> queryChidrenUser(@RequestParam(required = true) int uid) {
		List<UserVo> userVo = userService.queryChidrenUser(uid);
		return new ResponseEntity<List<UserVo>>(userVo, HttpStatus.OK);
	}
	
	/**
	 * 用户上传图片
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/picture", method = {RequestMethod.POST,RequestMethod.GET})
    public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
		userService.uploadPicture(request, response);
	}
	
	/**
	 * 用户地址更新
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/useraddress", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserResp> userAddress(@RequestBody OrderInfoForm form) {
		UserResp br = userService.userAddress(form);
		return new ResponseEntity<UserResp>(br, HttpStatus.OK);
	}
	
	/**
	 * 获取用户地址
	 * @param openid
	 * @return
	 */
	@RequestMapping(value = "/queryaddress", method = {RequestMethod.POST,RequestMethod.GET})
	public @ResponseBody ResponseEntity<UserReceivingAddressVo> queryAddressByOpenid(@RequestParam(required = true) String openid) {
		UserReceivingAddressVo br = userService.queryAddressByOpenid(openid);
		return new ResponseEntity<UserReceivingAddressVo>(br, HttpStatus.OK);
	}
	
}
