package com.drag.cstgroup.marketing.ms.task;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drag.cstgroup.marketing.ms.dao.MsGoodsDao;
import com.drag.cstgroup.marketing.ms.entity.MsGoods;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务查询活动是否结束，每分钟跑一次
 * @author longyunbo
 *
 */
@Slf4j
@Component
public class MsIsEndCheckTask {

	@Autowired
	MsGoodsDao msGoodsDao;

	@Scheduled(cron = "${jobs.isEndCheckTask.schedule}")
	public void find() {
		try {
			List<MsGoods> MsGoodsList = msGoodsDao.findByIsEnd(0);
			for (MsGoods msGoods : MsGoodsList) {
				Date endTime = msGoods.getEndTime();
				Date nowTime = new Timestamp(System.currentTimeMillis());
				if(nowTime.after(endTime)) {
					msGoods.setIsEnd(1);
					msGoods.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					msGoodsDao.saveAndFlush(msGoods);
					log.info("定时任务处理成功，更新数据{}", msGoods);
				}
			}
		} catch (Exception e) {
			log.error("定时异常{}", e);
		}

	}

}
