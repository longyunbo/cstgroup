package com.drag.cstgroup.user.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.marketing.kj.dao.KjGoodsDao;
import com.drag.cstgroup.marketing.kj.dao.KjUserDao;
import com.drag.cstgroup.marketing.kj.entity.KjGoods;
import com.drag.cstgroup.marketing.kj.entity.KjUser;
import com.drag.cstgroup.marketing.pt.dao.PtGoodsDao;
import com.drag.cstgroup.marketing.pt.dao.PtUserDao;
import com.drag.cstgroup.marketing.pt.entity.PtGoods;
import com.drag.cstgroup.marketing.pt.entity.PtUser;
import com.drag.cstgroup.marketing.zl.dao.ZlGoodsDao;
import com.drag.cstgroup.marketing.zl.dao.ZlUserDao;
import com.drag.cstgroup.marketing.zl.entity.ZlGoods;
import com.drag.cstgroup.marketing.zl.entity.ZlUser;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserRankLevelDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserRankLevel;
import com.drag.cstgroup.user.form.UserForm;
import com.drag.cstgroup.user.resp.UserResp;
import com.drag.cstgroup.user.vo.ActivityVo;
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
	private PtUserDao ptUserDao;
	@Autowired
	private ZlUserDao zlUserDao;
	@Autowired
	private KjUserDao kjUserDao;
	@Autowired
	private PtGoodsDao ptGoodsDao;
	@Autowired
	private ZlGoodsDao zlGoodsDao;
	@Autowired
	private KjGoodsDao kjGoodsDao;
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
			
			BeanUtils.copyProperties(form, user);
			user.setCreateTime(new Timestamp(System.currentTimeMillis()));
			user.setRankLevel(1);
			userDao.save(user);
			baseResp.setReturnCode(Constant.SUCCESS);
			baseResp.setErrorMessage("新增用户成功!");
		} catch (Exception e) {
			log.error("新增用户信息异常{}",e);
			baseResp.setReturnCode(Constant.FAIL);
			baseResp.setErrorMessage("系统异常!");
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
				BeanUtils.copyProperties(user, userVo);
			}
		} catch (Exception e) {
			log.error("检查权限异常,{}",e);
		}
		return userVo;
	}
	
	
	public List<ActivityVo> queryActivityByOpenid(String openid) {
		List<ActivityVo> actList = new ArrayList<ActivityVo>();
		User user = userDao.findByOpenid(openid);
		int uid = user.getId();
		List<PtUser> ptList = ptUserDao.findByUid(uid);
		for(PtUser pt : ptList) {
			int goodsId = pt.getPtgoodsId();
			PtGoods goods = ptGoodsDao.findGoodsDetail(goodsId);
			ActivityVo vo = new ActivityVo(); 
			vo.setGoodsId(goodsId);
			vo.setGoodsName(goods.getPtgoodsName());
			vo.setType(Constant.TYPE_PT);
			vo.setStatus(pt.getPtstatus());
			vo.setPrice(goods.getPrice());
			vo.setDefPrice(goods.getPtPrice());
			vo.setSize(goods.getPtSize());
			vo.setStartTime(DateUtil.format(goods.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setEndTime(DateUtil.format(goods.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setGoodsNumber(goods.getPtgoodsNumber());
			vo.setDescription(goods.getDescription());
			vo.setContent(goods.getContent());
			vo.setDragBone(goods.getDragBone());
			vo.setExp(goods.getExp());
			vo.setGoodsThumb(goods.getPtgoodsThumb());
			vo.setCreateTime(DateUtil.format(goods.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setIsEnd(goods.getIsEnd());
			vo.setTimes(goods.getPtTimes());
			vo.setSuccTimes(goods.getPtSuccTimes());
			actList.add(vo);
		}
		
		List<KjUser> kjList = kjUserDao.findByUid(uid);
		for(KjUser kj : kjList) {
			int goodsId = kj.getKjgoodsId();
			KjGoods goods = kjGoodsDao.findGoodsDetail(goodsId);
			ActivityVo vo = new ActivityVo(); 
			vo.setGoodsId(goodsId);
			vo.setGoodsName(goods.getKjgoodsName());
			vo.setType(Constant.TYPE_KJ);
			vo.setStatus(kj.getKjstatus());
			vo.setPrice(goods.getPrice());
			vo.setDefPrice(goods.getKjPrice());
			vo.setSize(goods.getKjSize());
			vo.setStartTime(DateUtil.format(goods.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setEndTime(DateUtil.format(goods.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setGoodsNumber(goods.getKjgoodsNumber());
			vo.setDescription(goods.getDescription());
			vo.setContent(goods.getContent());
			vo.setDragBone(goods.getDragBone());
			vo.setExp(goods.getExp());
			vo.setGoodsThumb(goods.getKjgoodsThumb());
			vo.setCreateTime(DateUtil.format(goods.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setIsEnd(goods.getIsEnd());
			vo.setTimes(goods.getKjTimes());
			vo.setSuccTimes(goods.getKjSuccTimes());
			actList.add(vo);
		}
		List<ZlUser> ZlList = zlUserDao.findByUid(uid);
		for(ZlUser zl : ZlList) {
			int goodsId = zl.getZlgoodsId();
			ZlGoods goods = zlGoodsDao.findGoodsDetail(goodsId);
			ActivityVo vo = new ActivityVo(); 
			vo.setGoodsId(goodsId);
			vo.setGoodsName(goods.getZlgoodsName());
			vo.setType(Constant.TYPE_KJ);
			vo.setStatus(zl.getZlstatus());
			vo.setPrice(BigDecimal.ZERO);
			vo.setDefPrice(goods.getZlPrice());
			vo.setSize(goods.getZlSize());
			vo.setStartTime(DateUtil.format(goods.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setEndTime(DateUtil.format(goods.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setGoodsNumber(goods.getZlgoodsNumber());
			vo.setDescription(goods.getDescription());
			vo.setContent(goods.getContent());
			vo.setDragBone(goods.getDragBone());
			vo.setExp(goods.getExp());
			vo.setGoodsThumb(goods.getZlgoodsThumb());
			vo.setCreateTime(DateUtil.format(goods.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			vo.setIsEnd(goods.getIsEnd());
			vo.setTimes(goods.getZlTimes());
			vo.setSuccTimes(goods.getZlSuccTimes());
			actList.add(vo);
		}
		return actList;
	}
	
}
