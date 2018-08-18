package com.drag.cstgroup.user.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drag.cstgroup.common.Constant;
import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.user.dao.UserDao;
import com.drag.cstgroup.user.dao.UserTicketDao;
import com.drag.cstgroup.user.dao.UserTicketRecordDao;
import com.drag.cstgroup.user.dao.UserTicketTemplateDao;
import com.drag.cstgroup.user.entity.User;
import com.drag.cstgroup.user.entity.UserTicket;
import com.drag.cstgroup.user.entity.UserTicketRecord;
import com.drag.cstgroup.user.entity.UserTicketTemplate;
import com.drag.cstgroup.user.form.UserTicketForm;
import com.drag.cstgroup.user.resp.UserTicketResp;
import com.drag.cstgroup.user.vo.UserTicketVo;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserTicketService {

	@Autowired
	UserTicketDao userTicketDao;
	@Autowired
	UserTicketRecordDao userTicketRecordDao;
	@Autowired
	UserTicketTemplateDao userTicketTemplateDao;
	@Autowired
	private UserDao userDao;
	
	/**
	 * 卡券列表
	 * @param openid
	 * @param type
	 * @return
	 */
	public List<UserTicketVo> listTicket(String openid) {
		List<UserTicketVo> ticketResp = new ArrayList<UserTicketVo>();
		User user = userDao.findByOpenid(openid);
		if(user != null) {
			int uid = user.getId();
			List<UserTicket> ticketList = userTicketDao.findByUid(uid);
			if (ticketList != null && ticketList.size() > 0) {
				for (UserTicket ticket : ticketList) {
					UserTicketVo resp = new UserTicketVo();
					BeanUtils.copyProperties(ticket, resp,new String[]{"createTime", "updateTime"});
					resp.setCreateTime((DateUtil.format(ticket.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
					ticketResp.add(resp);
				}
			}
		}
		return ticketResp;
	}
	
	/**
	 * 发送卡券
	 * @param form
	 */
	@Transactional
	public UserTicketResp sendTicket(UserTicketForm form) {
		UserTicketResp resp = new UserTicketResp();
		try {
			int goodsId = form.getGoodsId();
			String type= form.getType();
			String openid = form.getOpenid();
			User user = userDao.findByOpenid(openid);
			if(user == null) {
				resp.setReturnCode(Constant.USERNOTEXISTS);
				resp.setErrorMessage("该用户不存在!");
				return resp;
			}
			//获取系统用户编号
			int uid = user.getId();
			UserTicketTemplate  template = userTicketTemplateDao.findByGoodsIdAndType(goodsId, type);
			if(template != null) {
				UserTicket ticket = new UserTicket();
				BeanUtils.copyProperties(template, ticket);
				ticket.setUid(uid);
				ticket.setStatus(UserTicket.STATUS_NO);
				userTicketDao.save(ticket);
				resp.setReturnCode(Constant.SUCCESS);
				resp.setErrorMessage("卡券发送成功!");
			}
		} catch (Exception e) {
			log.error("系统异常,{}",e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
	}
	
	/**
	 * 核销卡券
	 * @param form
	 */
	@Transactional
	public UserTicketResp destoryTicket(int ticketId) {
		UserTicketResp resp = new UserTicketResp();
		try {
			UserTicket ticket = userTicketDao.findOne(ticketId);
			if(ticket != null) {
				
				if(ticket.getStatus() == UserTicket.STATUS_YES) {
					resp.setReturnCode(Constant.TICKET_DESTORY);
					resp.setErrorMessage("该卡券已经被核销!");
					return resp;
				}
				
				if(ticket.getStatus() == UserTicket.STATUS_OVER) {
					resp.setReturnCode(Constant.TICKET_OVER);
					resp.setErrorMessage("该卡券已经过期!");
					return resp;
				}
				
				//修改成已使用
				ticket.setStatus(UserTicket.STATUS_YES);
				userTicketDao.saveAndFlush(ticket);
				
				UserTicketRecord ticketRecord = new UserTicketRecord();
				ticketRecord.setId(ticketRecord.getId());
				ticketRecord.setTicketId(ticketId);
				ticketRecord.setCreateTime(new Timestamp(System.currentTimeMillis()));
				BeanUtils.copyProperties(ticket, ticketRecord);
				userTicketRecordDao.save(ticketRecord);
				
				resp.setReturnCode(Constant.SUCCESS);
				resp.setErrorMessage("卡券核销成功!");
			}else {
				resp.setReturnCode(Constant.TICKETNOTEXISTS);
				resp.setErrorMessage("卡券不存在");
			}
		} catch (Exception e) {
			log.error("系统异常,{}", e);
			throw AMPException.getException("系统异常!");
		}
		return resp;
		
	}
	
}
