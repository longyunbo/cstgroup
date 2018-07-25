package com.drag.cstgroup.marketing.pt.task;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.marketing.pt.dao.PtGoodsDao;
import com.drag.cstgroup.marketing.pt.dao.PtOrderDao;
import com.drag.cstgroup.marketing.pt.dao.PtUserDao;
import com.drag.cstgroup.marketing.pt.entity.PtGoods;
import com.drag.cstgroup.marketing.pt.entity.PtOrder;
import com.drag.cstgroup.marketing.pt.entity.PtUser;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务查询活动是否结束，每分钟跑一次
 * @author longyunbo
 *
 */
@Slf4j
@Component
public class IsEndCheckTask {

	@Autowired
	PtGoodsDao ptGoodsDao;
	@Autowired
	PtUserDao ptUserDao;
	@Autowired
	PtOrderDao ptOrderDao;

	@Scheduled(cron = "${jobs.isEndCheckTask.schedule}")
	@Transactional
	public void find() {
		try {
			List<PtGoods> ptGoodsList = ptGoodsDao.findByIsEnd(0);
			for (PtGoods ptGoods : ptGoodsList) {
				Date endTime = ptGoods.getEndTime();
				Date nowTime = new Timestamp(System.currentTimeMillis());
				if(nowTime.after(endTime)) {
					ptGoods.setIsEnd(1);
					ptGoods.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					ptGoodsDao.saveAndFlush(ptGoods);
					log.info("定时任务处理成功，更新数据{}", ptGoods);
				}
				
				int goodsId = ptGoods.getPtgoodsId();
				//查询拼团中的人数
				List<PtUser> ptList = ptUserDao.findByPtGoodsIdAndStatus(goodsId,PtUser.PTSTATUS_MIDDLE);
				Set<String> ptcodes = new HashSet<String>();
				for(PtUser ku : ptList) {
					//把在拼团中的状态的修改成失败
					ku.setPtstatus(PtUser.PTSTATUS_FAIL);
					ptUserDao.saveAndFlush(ku);
					ptcodes.add(ku.getPtcode());
				}
				
				if(ptcodes != null && ptcodes.size() > 0) {
					//拼团的数量 
					int allNumber = 0;
					List<PtOrder> orders = ptOrderDao.findByPtCodeIn(ptcodes);
					for(PtOrder order : orders) {
						int number = order.getNumber();
						allNumber = allNumber + number;
					}
					//回滚库存
					int ptgoodsNumber = ptGoods.getPtgoodsNumber();
					ptGoods.setPtgoodsNumber(ptgoodsNumber + allNumber);
					ptGoodsDao.saveAndFlush(ptGoods);
				}
			}
		} catch (Exception e) {
			log.error("定时异常{}", e);
			throw AMPException.getException("定时任务异常!");
		}

	}

}
