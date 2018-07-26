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
import com.drag.cstgroup.user.dao.ScoreGoodsDao;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserScoreRecordDao;
import com.drag.cstgroup.user.dao.UserScoreUsedRecordDao;
import com.drag.cstgroup.user.dao.UserTicketTemplateDao;
import com.drag.cstgroup.user.entity.ScoreGoods;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserScoreRecord;
import com.drag.cstgroup.user.entity.UserScoreUsedRecord;
import com.drag.cstgroup.user.entity.UserTicketTemplate;
import com.drag.cstgroup.user.form.ScoreForm;
import com.drag.cstgroup.user.form.UserTicketForm;
import com.drag.cstgroup.user.vo.ScoreGoodsVo;
import com.drag.cstgroup.user.vo.UserScoreUsedRecordVo;
import com.drag.cstgroup.user.vo.UserTicketTemplateVo;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScoreGoodsService {

	@Autowired
	private ScoreGoodsDao drGoodsDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserTicketService userTicketService;
	@Autowired
	private UserScoreRecordDao userDragRecordDao;
	@Autowired
	private UserScoreUsedRecordDao userDragUsedRecordDao;
	@Autowired
	private UserTicketTemplateDao userTicketTemplateDao;

	/**
	 * 查询所有的积分兑换商品(积分兑换中心)
	 * @return
	 */
	public List<ScoreGoodsVo> listGoods() {
		List<ScoreGoodsVo> goodsResp = new ArrayList<ScoreGoodsVo>();
		List<ScoreGoods> goodsList = drGoodsDao.findAll();
		if (goodsList != null && goodsList.size() > 0) {
			for (ScoreGoods drgoods : goodsList) {
				ScoreGoodsVo resp = new ScoreGoodsVo();
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
	 * 查询积分详情商品
	 * @return
	 */
	public UserTicketTemplateVo goodsDetail(int goodsId) {
		UserTicketTemplateVo detailVo = new UserTicketTemplateVo();
		UserTicketTemplate template = userTicketTemplateDao.findByGoodsIdAndType(goodsId, Constant.TYPE_DR);
		ScoreGoods dragGoods = drGoodsDao.findGoodsDetail(goodsId);
		BeanUtils.copyProperties(template, detailVo,new String[]{"createTime", "updateTime"});
		detailVo.setCreateTime((DateUtil.format(dragGoods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
		return detailVo;
	}
	
	/**
	 * 查询积分兑换记录
	 * @param openid
	 * @return
	 */
	public List<UserScoreUsedRecordVo> listRecord(String openid) {
		List<UserScoreUsedRecordVo> goodsResp = new ArrayList<UserScoreUsedRecordVo>();
		User user = userDao.findByOpenid(openid);
		List<UserScoreUsedRecord> records = userDragUsedRecordDao.findByUidAndType(user.getId(),Constant.TYPE_DR);
		for(UserScoreUsedRecord record : records) {
			UserScoreUsedRecordVo vo = new UserScoreUsedRecordVo();
			ScoreGoods goods = drGoodsDao.findGoodsDetail(record.getGoodsId()); 
			BeanUtils.copyProperties(record, vo,new String[]{"createTime", "updateTime"});
			vo.setGoodsName(goods.getGoodsName());
			vo.setCreateTime((DateUtil.format(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
			goodsResp.add(vo);
		}
		return goodsResp;
	}
	
	
	public List<UserScoreUsedRecordVo> listAllRecord(String openid) {
		List<UserScoreUsedRecordVo> goodsResp = new ArrayList<UserScoreUsedRecordVo>();
		User user = userDao.findByOpenid(openid);
		int uid = user.getId();
		
		List<UserScoreUsedRecord> userRecords = userDragUsedRecordDao.findByUid(uid);
		if(userRecords != null & userRecords.size() > 0) {
			for(UserScoreUsedRecord record : userRecords) {
				UserScoreUsedRecordVo vo = new UserScoreUsedRecordVo();
				BeanUtils.copyProperties(record, vo,new String[]{"createTime", "updateTime"});
				vo.setScore(record.getScore());
				vo.setUsedscore(-record.getUsedscore());
				vo.setGoodsName(record.getGoodsName());
				vo.setCreateTime((DateUtil.format(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				goodsResp.add(vo);
			}
		}
		
		List<UserScoreRecord> records = userDragRecordDao.findByUid(uid);
		if(records != null & records.size() > 0) {
			for(UserScoreRecord record : records) {
				UserScoreUsedRecordVo vo = new UserScoreUsedRecordVo();
				BeanUtils.copyProperties(record, vo,new String[]{"createTime", "updateTime"});
				vo.setScore(record.getScore());
				vo.setUsedscore(record.getAvailablescore());
				vo.setGoodsName(record.getGoodsName());
				vo.setCreateTime((DateUtil.format(record.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				goodsResp.add(vo);
			}
		}
		return goodsResp;
	}
	
	
	/**
	 * 积分立即兑换优惠券
	 * 1、减少用户积分
	 * 2、积分使用记录表
	 * 3、发送卡券
	 * @return
	 */
	@Transactional
	public BaseResponse exchange(ScoreForm form) {
		BaseResponse resp = new BaseResponse();
		try {
			int goodsId = form.getGoodsId();
			String openid = form.getOpenid();
			int score = form.getScore();
			User user = userDao.findByOpenid(openid);
			
			ScoreGoods dragGoods = drGoodsDao.findGoodsDetail(goodsId);
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
			Boolean flag = this.delscore(user,score);
			if(!flag) {
				resp.setReturnCode(Constant.STOCK_FAIL);
				resp.setErrorMessage("积分不足！");
				log.error("该用户积分不足,openid:{}",openid);
				return resp;
			}
			
			this.addDragUsedRecord(user, goodsId,dragGoods.getGoodsName(),Constant.TYPE_DR, score);
			
			UserTicketForm uForm = new UserTicketForm();
			uForm.setGoodsId(goodsId);
			uForm.setType(Constant.TYPE_DR);
			uForm.setOpenid(openid);
			userTicketService.sendTicket(uForm);
			resp.setReturnCode(Constant.SUCCESS);
			resp.setErrorMessage("使用积分成功！");
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
		return resp;
	}
	
	
	/**
	 * 用户减积分
	 * @param goods
	 * @param number
	 * @return
	 */
	public Boolean delscore(User user, int number) {
		boolean flag = false;
		int score = user.getScore();
		if (score - number < 0) {
			// 库存不足
			flag = false;
		} else {
			flag = true;
			int nowGoodsNum = score - number;
			user.setScore(nowGoodsNum);
			userDao.saveAndFlush(user);
		}
		return flag;
	}
	
	/**
	 * 用户参加各种活动插入数据
	 * 1、用户加积分，经验值
	 * 2、会员积分记录表
	 * @param user
	 * @param number
	 * @return
	 */
	@Transactional
	public void addscore(User user,int goodsId,String goodsName,String type, int score,int exp) {
		try {
			int uscore = user.getScore();
			int uexp = user.getExp();
			int nowscore = uscore + score;
			int nowExp =  uexp + exp;
			user.setScore(nowscore);
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
			//会员积分记录表
			UserScoreRecord dragRecord = new UserScoreRecord();
			dragRecord.setId(dragRecord.getId());
			dragRecord.setUid(user.getId());
			dragRecord.setGoodsId(goodsId);
			dragRecord.setGoodsName(goodsName);
			dragRecord.setType(type);
			//当前当前积分
			dragRecord.setScore(nowscore);
			//获得积分
			dragRecord.setAvailablescore(score);
			dragRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
			//插入会员积分记录表
			userDragRecordDao.save(dragRecord);
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
	}
	
	/**
	 * 积分使用记录入库
	 * @param user
	 * @param goods_id
	 * @param type
	 * @param score
	 */
	@Transactional
	public void addDragUsedRecord(User user,int goodsId,String goodsName,String type, int score) {
		try {
			int uscore = user.getScore();
			int nowscore = uscore - score;
			user.setScore(nowscore);
			userDao.saveAndFlush(user);
			//会员积分记录表
			UserScoreUsedRecord dragRecord = new UserScoreUsedRecord();
			dragRecord.setId(dragRecord.getId());
			dragRecord.setUid(user.getId());
			dragRecord.setGoodsId(goodsId);
			dragRecord.setGoodsName(goodsName);
			dragRecord.setType(type);
			//当前当前积分
			dragRecord.setScore(nowscore);
			//获得积分
			dragRecord.setUsedscore(score);
			dragRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
			//插入会员积分使用记录表
			userDragUsedRecordDao.save(dragRecord);
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
	}
	
	
}
