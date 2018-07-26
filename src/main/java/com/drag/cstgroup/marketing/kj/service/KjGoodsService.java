package com.drag.cstgroup.marketing.kj.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.marketing.kj.dao.KjGoodsDao;
import com.drag.cstgroup.marketing.kj.dao.KjUserDao;
import com.drag.cstgroup.marketing.kj.entity.KjGoods;
import com.drag.cstgroup.marketing.kj.entity.KjUser;
import com.drag.cstgroup.marketing.kj.form.KjGoodsForm;
import com.drag.cstgroup.marketing.kj.resp.KjGoodsResp;
import com.drag.cstgroup.marketing.kj.vo.KjGoodsDetailVo;
import com.drag.cstgroup.marketing.kj.vo.KjGoodsVo;
import com.drag.cstgroup.marketing.pt.entity.PtUser;
import com.drag.cstgroup.marketing.zl.entity.ZlUser;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserTicketDao;
import com.drag.cstgroup.user.dao.UserTicketTemplateDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserTicket;
import com.drag.cstgroup.user.entity.UserTicketTemplate;
import com.drag.cstgroup.user.service.ScoreGoodsService;
import com.drag.cstgroup.user.service.UserService;
import com.drag.cstgroup.user.vo.UserVo;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.DateUtil;
import com.drag.cstgroup.utils.MoneyUtil;
import com.drag.cstgroup.utils.StringUtil;
import com.drag.cstgroup.utils.WxUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KjGoodsService {

	@Autowired
	private KjGoodsDao kjGoodsDao;
	@Autowired
	private KjUserDao kjUserDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private UserTicketTemplateDao userTicketTemplateDao;
	@Autowired
	private UserTicketDao userTicketDao;
	@Autowired
	private ScoreGoodsService dragGoodsService;
	@Autowired
	private UserService userService;
	@Value("${weixin.url.kj.templateid}")
	private String templateid;

	/**
	 * 查询所有的砍价商品(砍价列表)
	 * @return
	 */
	public List<KjGoodsVo> listGoods() {
		List<KjGoodsVo> goodsResp = new ArrayList<KjGoodsVo>();
		List<KjGoods> goodsList = kjGoodsDao.findAll();
		if (goodsList != null && goodsList.size() > 0) {
			for (KjGoods kjgoods : goodsList) {
				KjGoodsVo resp = new KjGoodsVo();
				BeanUtils.copyProperties(kjgoods, resp,new String[]{"createTime", "updateTime","startTime","endTime"});
				resp.setCreateTime((DateUtil.format(kjgoods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
				resp.setUpdateTime((DateUtil.format(kjgoods.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
				resp.setStartTime((DateUtil.format(kjgoods.getStartTime(), "yyyy-MM-dd HH:mm:ss")));
				resp.setEndTime((DateUtil.format(kjgoods.getEndTime(), "yyyy-MM-dd HH:mm:ss")));
				goodsResp.add(resp);
			}
		}
		return goodsResp;
	}
	
	
	/**
	 * 查询砍价详情商品(查询所有发起砍价的用户)
	 * @return
	 */
	public KjGoodsDetailVo goodsDetail(int goodsId) {
		List<UserVo> grouperList = new ArrayList<UserVo>();
		KjGoodsDetailVo detailVo = new KjGoodsDetailVo();
		List<KjUser> groupers = new ArrayList<KjUser>();
		KjGoods goods = kjGoodsDao.findGoodsDetail(goodsId);
		if(goods != null) {
			this.copyProperties(goods, detailVo);
			//根据商品编号查询砍价团长
			groupers = kjUserDao.findByKjGoodsIdAndIsHead(goodsId, KjUser.ISHEADER_YES);
			if(groupers != null && groupers.size() > 0) {
				for(KjUser pu : groupers) {
					UserVo userVo = new UserVo();
					int groupId = pu.getGrouperId();
					User user = userDao.findOne(groupId);
					userVo.setPrice(pu.getPrice());
					userVo.setCode(pu.getKjcode());
					if(user != null) {
						BeanUtils.copyProperties(user, userVo);
						grouperList.add(userVo);
					}
				}
			}
			detailVo.setGroupers(grouperList);
		}
		return detailVo;
	}
	
	/**
	 * 查询砍价活动是否结束
	 * @param goodsId
	 * @return
	 */
	public Boolean checkEnd(int goodsId) {
		boolean endFlag = false;
		KjGoods goods = kjGoodsDao.findGoodsDetail(goodsId);
		if(goods != null) {
			int isEnd = goods.getIsEnd();
			if(isEnd == 1) {
				endFlag = true;
			}else {
				endFlag = false;
			}
		}
		return endFlag;
	}
	
	
	/**
	 * 本人发起砍价
	 * @return
	 */
	@Transactional
	public KjGoodsResp collage(KjGoodsForm form) {
		KjGoodsResp baseResp = new KjGoodsResp();
		try {
			int kjgoodsId = form.getKjgoodsId();
			
			String openid = form.getOpenid();
			User user = userDao.findByOpenid(openid);
			KjGoods goods = kjGoodsDao.findGoodsDetail(kjgoodsId);
			if(user == null) {
				baseResp.setReturnCode(Constant.USERNOTEXISTS);
				baseResp.setErrorMessage("该用户不存在!");
				return baseResp;
			}
			if(goods == null) {
				baseResp.setReturnCode(Constant.PRODUCTNOTEXISTS);
				baseResp.setErrorMessage("该商品编号不存在!");
				return baseResp;
			}
			
			Boolean authFlag =  userService.checkAuth(user, goods.getAuth());
			if(!authFlag) {
				baseResp.setReturnCode(Constant.AUTH_OVER);
				baseResp.setErrorMessage("该用户权限不够!");
				return baseResp;
			}
			
			//获取系统用户编号
			int uid = user.getId();
			
			List<KjUser> kjList = kjUserDao.findByUidAndKjgoodsIdAndIsHeadAndKjStatus(uid, kjgoodsId, KjUser.ISHEADER_YES, KjUser.PTSTATUS_MIDDLE);
			if(kjList != null && kjList.size() > 0) {
				baseResp.setReturnCode(Constant.USERALREADYIN_FAIL);
				baseResp.setErrorMessage("该用户已经砍过此商品，请完成活动后再砍!");
				return baseResp;
			}
			
			//购买数量(默认1份)
			int number = 1;
			
			if(goods != null) {
				//减库存
				Boolean flag = this.delStock(goods,number);
				if(!flag) {
					baseResp.setReturnCode(Constant.STOCK_FAIL);
					baseResp.setErrorMessage("库存不足");
					log.error("该商品库存不足,kjgoodsId:{}",kjgoodsId);
					return baseResp;
				}
			}
			
//			//生成一个砍价编号
			String kjCode = StringUtil.uuid();
			//砍价用户表中也插入一条数据
			KjUser kjUser = new KjUser();
			kjUser.setUid(uid);
			kjUser.setGrouperId(uid);
			kjUser.setKjgoodsId(form.getKjgoodsId());
			kjUser.setKjgoodsName(goods.getKjgoodsName());
			kjUser.setKjcode(kjCode);
			kjUser.setIsHeader(KjUser.ISHEADER_YES);
			kjUser.setKjSize(goods.getKjSize());
			kjUser.setKjstatus(KjUser.PTSTATUS_MIDDLE);
			kjUser.setKjPrice(goods.getKjPrice());
			kjUser.setFormId(form.getFormId());
			//付款金额，随机生成一个数字，
			
			BigDecimal kjPrice = goods.getKjPrice();
			int num = goods.getKjSize();
			float price = MoneyUtil.randomRedPacket(kjPrice.floatValue(), 1, 20, num);
			BigDecimal priceB = new BigDecimal(price);
			kjUser.setPrice(priceB);
			
			kjUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
			kjUserDao.save(kjUser);
			
			//新增砍价次数
			this.addKjTimes(goods);
			
			//返回参数
			baseResp.setKjgoodsId(kjgoodsId);
			baseResp.setKjcode(kjCode);
			baseResp.setReturnCode(Constant.SUCCESS);
			baseResp.setErrorMessage("砍价成功!");
			baseResp.setPrice(priceB.setScale(2,BigDecimal.ROUND_HALF_UP));
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			baseResp.setReturnCode(Constant.FAIL);
			baseResp.setErrorMessage("系统异常!");
			throw AMPException.getException("系统异常!");
		}
		
		return baseResp;
	}
	
	public int mt_rand(int min,int max) {
		int randNumber = 0;
		Random rand = new Random();
		randNumber = rand.nextInt(max - min + 1) + min;
		return randNumber;
	}
	
	/**
	 * 本人(好友)查询砍价详情
	 * @param kjcode
	 * @return
	 */
	public KjGoodsDetailVo myDetail(String kjcode) {
		List<UserVo> grouperList = new ArrayList<UserVo>();
		KjGoodsDetailVo detailVo = new KjGoodsDetailVo();
		List<KjUser> groupers = new ArrayList<KjUser>();
		
		List<KjUser> kjUserList = kjUserDao.findByKjCodeAndIsHead(kjcode,KjUser.ISHEADER_YES);
		if(kjUserList != null && kjUserList.size() > 0) {
			//团长
			KjUser grouper = kjUserList.get(0);
			if(grouper != null) {
				int kjgoodsId = grouper.getKjgoodsId();
				KjGoods goods = kjGoodsDao.findGoodsDetail(kjgoodsId);
				if(goods != null ) {
					this.copyProperties(goods, detailVo);
					//根据商品编号，砍价code，查询好友砍价信息
					groupers = kjUserDao.findByKjCode(kjcode);
					if(groupers != null && groupers.size() > 0) {
						for(KjUser pu : groupers) {
							UserVo userVo = new UserVo();
							userVo.setPrice(pu.getPrice());
							userVo.setCode(pu.getKjcode());
							int uid = pu.getUid();
							User user = userDao.findOne(uid);
							if(user != null) {
								BeanUtils.copyProperties(user, userVo);
								grouperList.add(userVo);
							}
						}
					}
				}
				detailVo.setGroupers(grouperList);
			}
		}
		return detailVo;
	}
	
	/**
	 * 分享给好友，好友砍价
	 * @param form
	 * @return
	 */
	@Transactional
	public KjGoodsResp friendcollage(KjGoodsForm form) {
		KjGoodsResp baseResp = new KjGoodsResp();
		try {
			//砍价规模
			int kjSize = 0;
			//已经砍价的人数
			int grouperSize = 0;
			//砍价编号
			String kjCode = form.getKjCode();
			//商品编号
			int kjgoodsId = form.getKjgoodsId();
			
			String openid = form.getOpenid();
			User user = userDao.findByOpenid(openid);
			if(user == null) {
				baseResp.setReturnCode(Constant.USERNOTEXISTS);
				baseResp.setErrorMessage("该用户不存在!");
				return baseResp;
			}
			//获取系统用户编号
			int uid = user.getId();
			
			KjGoods goods = kjGoodsDao.findGoodsDetail(kjgoodsId);
			if(goods == null) {
				baseResp.setReturnCode(Constant.PRODUCTNOTEXISTS);
				baseResp.setErrorMessage("该商品编号不存在!");
				return baseResp;
			}
			
			Boolean authFlag =  userService.checkAuth(user, goods.getAuth());
			if(!authFlag) {
				baseResp.setReturnCode(Constant.AUTH_OVER);
				baseResp.setErrorMessage("该用户权限不够!");
				return baseResp;
			}
			//同一个用户砍价校验
			KjUser kjuser = kjUserDao.findByKjGoodsIdAndUidAndKjCode(kjgoodsId, uid, kjCode);
			if(kjuser != null) {
				baseResp.setReturnCode(Constant.USERALREADYIN_FAIL);
				baseResp.setErrorMessage("该用户已经砍过此商品，不能再砍价!");
				return baseResp;
			}
			//根据砍价编号查询
			List<KjUser> kjList = kjUserDao.findByKjCode(kjCode);
			//获取团长
			KjUser grouper = null;
			for(KjUser us: kjList) {
				if(us.getIsHeader() == PtUser.ISHEADER_YES) {
					grouper = us;
				}
			}
			//砍价已完成校验
			if(kjList != null && kjList.size() > 0) {
				kjSize = goods.getKjSize();
				//已经砍价的人数
				grouperSize = kjList.size();
				if(grouperSize >= kjSize) {
					baseResp.setReturnCode(Constant.ACTIVITYALREADYDOWN_FAIL);
					baseResp.setErrorMessage("该团砍价已完成，不能再砍价!");
					return baseResp;
				}
			}else {
				baseResp.setReturnCode(Constant.ACTIVITYNOTEXISTS);
				baseResp.setErrorMessage("该砍价编号不存在!");
				return baseResp;
			}
			
			//购买数量
			int number = 1;
			if(goods != null) {
				//减库存
				Boolean flag = this.delStock(goods,number);
				if(!flag) {
					baseResp.setReturnCode(Constant.STOCK_FAIL);
					baseResp.setErrorMessage("库存不足");
					log.error("该商品库存不足,kjgoodsId:{}",kjgoodsId);
					return baseResp;
				}
			}
			
			//砍价用户表中也插入一条数据
			KjUser kjUser = new KjUser();
			kjUser.setId(kjUser.getId());
			kjUser.setUid(uid);
			kjUser.setGrouperId(grouper.getGrouperId());
			kjUser.setKjgoodsId(form.getKjgoodsId());
			kjUser.setKjgoodsName(goods.getKjgoodsName());
			kjUser.setKjcode(kjCode);
			kjUser.setIsHeader(KjUser.ISHEADER_NO);
			kjUser.setKjSize(goods.getKjSize());
			
			//商品默认价格
			BigDecimal kjPrice = goods.getKjPrice();
			
			BigDecimal alreadyPrice = BigDecimal.ZERO;
			for (KjUser kUser : kjList) {
				alreadyPrice = alreadyPrice.add(kUser.getPrice());
			}
			float price = MoneyUtil.randomRedPacket(kjPrice.subtract(alreadyPrice).floatValue(), 1, 20, kjSize - grouperSize);
			BigDecimal priceB = new BigDecimal(price);
			kjUser.setPrice(priceB);
			
			kjUser.setKjPrice(goods.getKjPrice());
			
			kjUser.setKjstatus(KjUser.PTSTATUS_MIDDLE);
			kjUser.setFormId(form.getFormId());
			kjUser.setCreateTime(new Timestamp(System.currentTimeMillis()));
			kjUserDao.save(kjUser);
			kjList.add(kjUser);
			//新增砍价次数
			this.addKjTimes(goods);
			//更新完成砍价人数，发送卡券
			this.updateSuccess(kjList,goods,kjCode);
			
			baseResp.setKjgoodsId(kjgoodsId);
			baseResp.setKjcode(kjCode);
			baseResp.setPrice(priceB.setScale(2,BigDecimal.ROUND_HALF_UP));
			baseResp.setReturnCode(Constant.SUCCESS);
			baseResp.setErrorMessage("砍价成功！");
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			baseResp.setReturnCode(Constant.FAIL);
			baseResp.setErrorMessage("系统异常!");
			throw AMPException.getException("系统异常!");
		}
		
		return baseResp;
	}
	
	
	/**
	 * 更新完成砍价人数
	 * @param form
	 */
	@Transactional
	public void updateSuccess(List<KjUser> kjList,KjGoods goods,String kjCode) {
		try {
			int kjSize = 0;
			int grouperSize = 0;
			if(kjList != null && kjList.size() > 0) {
				kjSize = goods.getKjSize();
				//已经砍价的人数
				grouperSize = kjList.size();
				if(grouperSize == kjSize) {
					//如果人数达到砍价人数规模，更新砍价状态为砍价成功
					kjUserDao.updateKjstatus(kjCode);
					int kjSuccTimes = goods.getKjSuccTimes();
					goods.setKjSuccTimes(kjSuccTimes + kjSize);
					kjGoodsDao.saveAndFlush(goods);
					
					String type = Constant.TYPE_KJ;
					UserTicketTemplate  template = userTicketTemplateDao.findByGoodsIdAndType(goods.getKjgoodsId(), type);
					
					Map<String,User> userMap = new HashMap<String,User>();
					List<Integer> ids = new ArrayList<>();
					
					KjUser grouper = null;
					for(KjUser user : kjList) {
						if(KjUser.ISHEADER_YES == user.getIsHeader()) {
							grouper = user;
						}
						ids.add(user.getUid());
					}
					List<User> userList = userDao.findByIdIn(ids);
					if(userList != null && userList.size() > 0) {
						for(User user : userList) {
							userMap.put(String.valueOf(user.getId()), user);
						}
					}
					
					
					for(KjUser user : kjList) {
						int isHeader = user.getIsHeader();
//						User us = userDao.findOne(user.getUid());
						String uid = String.valueOf(user.getUid());
						//新增积分
						dragGoodsService.addscore(userMap.get(uid),goods.getKjgoodsId(),goods.getKjgoodsName(),Constant.TYPE_KJ,goods.getScore(), goods.getExp());
						//给团长发送卡券
						if(ZlUser.ISHEADER_YES == isHeader) {
							if(template != null) {
								UserTicket ticket = new UserTicket();
								BeanUtils.copyProperties(template, ticket);
								ticket.setId(ticket.getId());
								ticket.setUid(user.getUid());
								ticket.setStatus(UserTicket.STATUS_NO);
								ticket.setCreateTime(new Timestamp(System.currentTimeMillis()));
								userTicketDao.save(ticket);
							}
							JSONObject json = new JSONObject();
							//openid
							json.put("touser", userMap.get(uid).getOpenid());
							json.put("template_id", templateid);
							json.put("page", "pages/bargain/bargaindetail/bargaindetail?shopid=" + user.getKjgoodsId());
							json.put("form_id", user.getFormId());
							//商品名称
							JSONObject keyword1 = new JSONObject();
							keyword1.put("value", goods.getKjgoodsName());
							keyword1.put("color", "#000000");
							//温馨提示
							JSONObject keyword2 = new JSONObject();
							keyword2.put("value", "砍价成功！");
							keyword2.put("color", "#000000");
							
							JSONObject data = new JSONObject();
							data.put("keyword1", keyword1);
							data.put("keyword2", keyword2);
							json.put("data", data);
							boolean result =  WxUtil.sendTemplateMsg(json);
							if(result) {
								user.setSendstatus(Constant.SENDSTATUS_SUCC);
							}else {
								user.setSendstatus(Constant.SENDSTATUS_FAIL);
							}
							kjUserDao.saveAndFlush(user);
							
						}
						
					}
				}
			}
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		
	}
	
	
	/**
	 * 砍价商品减库存
	 * @param goods
	 * @param number
	 * @return
	 */
	public Boolean delStock(KjGoods goods, int number) {
		boolean flag = false;
		int kjgoodsNumber = goods.getKjgoodsNumber();
		if (kjgoodsNumber - number < 0) {
			// 库存不足
			flag = false;
		} else {
			flag = true;
			int nowGoodsNum = kjgoodsNumber - number;
			goods.setKjgoodsNumber(nowGoodsNum);
			kjGoodsDao.saveAndFlush(goods);
		}
		return flag;
	}
	
	/**
	 * 增加砍价次数
	 * @param goods
	 * @param number
	 */
	public void addKjTimes(KjGoods goods) {
		int kjTimes = goods.getKjTimes();
		goods.setKjTimes(kjTimes + 1);
		kjGoodsDao.saveAndFlush(goods);
	}
	
	/**
	 * 提取copy方法
	 * @param goods
	 * @param detailVo
	 */
	public void copyProperties(KjGoods goods,KjGoodsDetailVo detailVo) {
		BeanUtils.copyProperties(goods, detailVo,new String[]{"createTime", "updateTime","startTime","endTime"});
		detailVo.setCreateTime((DateUtil.format(goods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setUpdateTime((DateUtil.format(goods.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setStartTime((DateUtil.format(goods.getStartTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setEndTime((DateUtil.format(goods.getEndTime(), "yyyy-MM-dd HH:mm:ss")));
	}
}
