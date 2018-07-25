package com.drag.cstgroup.user.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drag.cstgroup.common.BaseResponse;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.user.dao.DragGoodsDao;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserDragRecordDao;
import com.drag.cstgroup.user.dao.UserDragUsedRecordDao;
import com.drag.cstgroup.user.dao.UserTicketTemplateDao;
import com.drag.cstgroup.user.entity.DragGoods;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserDragRecord;
import com.drag.cstgroup.user.entity.UserDragUsedRecord;
import com.drag.cstgroup.user.entity.UserTicketTemplate;
import com.drag.cstgroup.user.form.DragBoneForm;
import com.drag.cstgroup.user.form.UserTicketForm;
import com.drag.cstgroup.user.vo.DragGoodsVo;
import com.drag.cstgroup.user.vo.UserDragUsedRecordVo;
import com.drag.cstgroup.user.vo.UserTicketTemplateVo;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DragGoodsService {

	@Autowired
	private DragGoodsDao drGoodsDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserTicketService userTicketService;
	@Autowired
	private UserDragRecordDao userDragRecordDao;
	@Autowired
	private UserDragUsedRecordDao userDragUsedRecordDao;
	@Autowired
	private UserTicketTemplateDao userTicketTemplateDao;

	/**
	 * 查询所有的恐龙骨兑换商品(恐龙骨兑换中心)
	 * @return
	 */
	public List<DragGoodsVo> listGoods() {
		List<DragGoodsVo> goodsResp = new ArrayList<DragGoodsVo>();
		List<DragGoods> goodsList = drGoodsDao.findAll();
		if (goodsList != null && goodsList.size() > 0) {
			for (DragGoods drgoods : goodsList) {
				DragGoodsVo resp = new DragGoodsVo();
				BeanUtils.copyProperties(drgoods, resp,new String[]{"createTime", "updateTime","startTime","endTime"});
				resp.setCreateTime((DateUtil.format(drgoods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				resp.setUpdateTime((DateUtil.format(drgoods.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
				resp.setStartTime((DateUtil.format(drgoods.getStartTime(), "yyyy-MM-dd HH:mm:ss")));
				resp.setEndTime((DateUtil.format(drgoods.getEndTime(), "yyyy-MM-dd HH:mm:ss")));
				goodsResp.add(resp);
			}
		}
		return goodsResp;
	}
	
	
	/**
	 * 查询恐龙骨详情商品
	 * @return
	 */
	public UserTicketTemplateVo goodsDetail(int goodsId) {
		UserTicketTemplateVo detailVo = new UserTicketTemplateVo();
		UserTicketTemplate template = userTicketTemplateDao.findByGoodsIdAndType(goodsId, Constant.TYPE_DR);
		DragGoods dragGoods = drGoodsDao.findGoodsDetail(goodsId);
		BeanUtils.copyProperties(template, detailVo,new String[]{"createTime", "updateTime"});
		detailVo.setCreateTime((DateUtil.format(dragGoods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
		return detailVo;
	}
	
	/**
	 * 查询恐龙骨兑换记录
	 * @param openid
	 * @return
	 */
	public List<UserDragUsedRecordVo> listRecord(String openid) {
		List<UserDragUsedRecordVo> goodsResp = new ArrayList<UserDragUsedRecordVo>();
		User user = userDao.findByOpenid(openid);
		List<UserDragUsedRecord> records = userDragUsedRecordDao.findByUidAndType(user.getId(),Constant.TYPE_DR);
		for(UserDragUsedRecord record : records) {
			UserDragUsedRecordVo vo = new UserDragUsedRecordVo();
			DragGoods goods = drGoodsDao.findGoodsDetail(record.getGoodsId()); 
			BeanUtils.copyProperties(record, vo,new String[]{"createTime", "updateTime"});
			vo.setGoodsName(goods.getDrgoodsName());
			vo.setCreateTime((DateUtil.format(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
			goodsResp.add(vo);
		}
		return goodsResp;
	}
	
	
	public List<UserDragUsedRecordVo> listAllRecord(String openid) {
		List<UserDragUsedRecordVo> goodsResp = new ArrayList<UserDragUsedRecordVo>();
		User user = userDao.findByOpenid(openid);
		int uid = user.getId();
		
		List<UserDragUsedRecord> userRecords = userDragUsedRecordDao.findByUid(uid);
		if(userRecords != null & userRecords.size() > 0) {
			for(UserDragUsedRecord record : userRecords) {
				UserDragUsedRecordVo vo = new UserDragUsedRecordVo();
				BeanUtils.copyProperties(record, vo,new String[]{"createTime", "updateTime"});
				vo.setDragBone(record.getDragBone());
				vo.setUsedDragBone(-record.getUsedDragBone());
				vo.setGoodsName(record.getGoodsName());
				vo.setCreateTime((DateUtil.format(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				goodsResp.add(vo);
			}
		}
		
		List<UserDragRecord> records = userDragRecordDao.findByUid(uid);
		if(records != null & records.size() > 0) {
			for(UserDragRecord record : records) {
				UserDragUsedRecordVo vo = new UserDragUsedRecordVo();
				BeanUtils.copyProperties(record, vo,new String[]{"createTime", "updateTime"});
				vo.setDragBone(record.getDragBone());
				vo.setUsedDragBone(record.getAvailableDragBone());
				vo.setGoodsName(record.getGoodsName());
				vo.setCreateTime((DateUtil.format(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				goodsResp.add(vo);
			}
		}
		return goodsResp;
	}
	
	
	/**
	 * 恐龙骨立即兑换优惠券
	 * 1、减少用户恐龙骨
	 * 2、恐龙骨使用记录表
	 * 3、发送卡券
	 * @return
	 */
	@Transactional
	public BaseResponse exchange(DragBoneForm form) {
		BaseResponse resp = new BaseResponse();
		try {
			int goodsId = form.getGoodsId();
			String openid = form.getOpenid();
			int dragBone = form.getDragBone();
			User user = userDao.findByOpenid(openid);
			
			DragGoods dragGoods = drGoodsDao.findGoodsDetail(goodsId);
			if(dragGoods == null) {
				resp.setReturnCode(Constant.PRODUCTNOTEXISTS);
				resp.setErrorMessage("该商品不存在!");
				return resp;
			}
			if(user == null) {
				resp.setReturnCode(Constant.USERNOTEXISTS);
				resp.setErrorMessage("该用户不存在!");
				return resp;
			}
			Boolean flag = this.delDragBone(user,dragBone);
			if(!flag) {
				resp.setReturnCode(Constant.STOCK_FAIL);
				resp.setErrorMessage("恐龙骨不足！");
				log.error("该用户恐龙骨不足,openid:{}",openid);
				return resp;
			}
			
			this.addDragUsedRecord(user, goodsId,dragGoods.getDrgoodsName(),Constant.TYPE_DR, dragBone);
			
			UserTicketForm uForm = new UserTicketForm();
			uForm.setGoodsId(goodsId);
			uForm.setType(Constant.TYPE_DR);
			uForm.setOpenid(openid);
			userTicketService.sendTicket(uForm);
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("使用恐龙骨成功！");
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
		return resp;
	}
	
	
	/**
	 * 用户减恐龙骨
	 * @param goods
	 * @param number
	 * @return
	 */
	public Boolean delDragBone(User user, int number) {
		boolean flag = false;
		int dragBone = user.getDragBone();
		if (dragBone - number < 0) {
			// 库存不足
			flag = false;
		} else {
			flag = true;
			int nowGoodsNum = dragBone - number;
			user.setDragBone(nowGoodsNum);
			userDao.saveAndFlush(user);
		}
		return flag;
	}
	
	/**
	 * 用户参加各种活动插入数据
	 * 1、用户加恐龙骨，经验值
	 * 2、会员恐龙骨记录表
	 * @param user
	 * @param number
	 * @return
	 */
	@Transactional
	public void addDragBone(User user,int goodsId,String goodsName,String type, int dragBone,int exp) {
		try {
			int udragBone = user.getDragBone();
			int uexp = user.getExp();
			int nowDragBone = udragBone + dragBone;
			int nowExp =  uexp + exp;
			user.setDragBone(nowDragBone);
			user.setExp(nowExp);
			//0恐龙蛋-注册，1幼年霸王龙=1000，2青年霸王龙=3000，3成年霸王龙=5000
			if(nowExp<1000) {
				user.setRankLevel(0);
			}else if(nowExp >= 1000 && nowExp < 3000) {
				user.setRankLevel(1);
			}else if(nowExp >= 3000 && nowExp < 5000) {
				user.setRankLevel(2);
			}else {
				user.setRankLevel(3);
			}
			userDao.saveAndFlush(user);
			//会员恐龙骨记录表
			UserDragRecord dragRecord = new UserDragRecord();
			dragRecord.setId(dragRecord.getId());
			dragRecord.setUid(user.getId());
			dragRecord.setGoodsId(goodsId);
			dragRecord.setGoodsName(goodsName);
			dragRecord.setType(type);
			//当前当前恐龙骨
			dragRecord.setDragBone(nowDragBone);
			//获得恐龙骨
			dragRecord.setAvailableDragBone(dragBone);
			dragRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
			//插入会员恐龙骨记录表
			userDragRecordDao.save(dragRecord);
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
	}
	
	/**
	 * 恐龙骨使用记录入库
	 * @param user
	 * @param goods_id
	 * @param type
	 * @param dragBone
	 */
	@Transactional
	public void addDragUsedRecord(User user,int goodsId,String goodsName,String type, int dragBone) {
		try {
			int udragBone = user.getDragBone();
			int nowDragBone = udragBone - dragBone;
			user.setDragBone(nowDragBone);
			userDao.saveAndFlush(user);
			//会员恐龙骨记录表
			UserDragUsedRecord dragRecord = new UserDragUsedRecord();
			dragRecord.setId(dragRecord.getId());
			dragRecord.setUid(user.getId());
			dragRecord.setGoodsId(goodsId);
			dragRecord.setGoodsName(goodsName);
			dragRecord.setType(type);
			//当前当前恐龙骨
			dragRecord.setDragBone(nowDragBone);
			//获得恐龙骨
			dragRecord.setUsedDragBone(dragBone);
			dragRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
			//插入会员恐龙骨使用记录表
			userDragUsedRecordDao.save(dragRecord);
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
	}
	
	
}
