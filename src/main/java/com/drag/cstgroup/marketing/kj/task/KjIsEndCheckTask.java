package com.drag.cstgroup.marketing.kj.task;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.marketing.kj.dao.KjGoodsDao;
import com.drag.cstgroup.marketing.kj.dao.KjUserDao;
import com.drag.cstgroup.marketing.kj.entity.KjGoods;
import com.drag.cstgroup.marketing.kj.entity.KjUser;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务查询活动是否结束，每分钟跑一次
 * @author longyunbo
 *
 */
@Slf4j
@Component
public class KjIsEndCheckTask {

	@Autowired
	KjGoodsDao kjGoodsDao;
	@Autowired
	KjUserDao kjUserDao;

	@Scheduled(cron = "${jobs.isEndCheckTask.schedule}")
	@Transactional
	public void find() {
		try {
			List<KjGoods> KjGoodsList = kjGoodsDao.findByIsEnd(0);
			for (KjGoods kjGoods : KjGoodsList) {
				Date endTime = kjGoods.getEndTime();
				Date nowTime = new Timestamp(System.currentTimeMillis());
				if(nowTime.after(endTime)) {
					kjGoods.setIsEnd(1);
					kjGoods.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					kjGoodsDao.saveAndFlush(kjGoods);
					log.info("定时任务处理成功，更新数据{}", kjGoods);
				}
				
				int goodsId = kjGoods.getKjgoodsId();
				//查询砍价中的人数
				List<KjUser> kjList = kjUserDao.findByKjGoodsIdAndKjstatus(goodsId,KjUser.PTSTATUS_MIDDLE);
				//砍价的默认数量为1个 
				int number = 0;
				for(KjUser ku : kjList) {
					//把在砍价中的状态修改成失败
					ku.setKjstatus(KjUser.PTSTATUS_FAIL);
					kjUserDao.saveAndFlush(ku);
					number ++ ;
				}
				//回滚库存
				int kjgoodsNumber = kjGoods.getKjgoodsNumber();
				kjGoods.setKjgoodsNumber(kjgoodsNumber + number);
				kjGoodsDao.saveAndFlush(kjGoods);
			}
		} catch (Exception e) {
			log.error("定时异常{}", e);
			throw AMPException.getException("定时任务异常!");
		}

	}

}
