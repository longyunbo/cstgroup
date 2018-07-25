package com.drag.cstgroup.marketing.zl.task;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.drag.cstgroup.common.exception.AMPException;
import com.drag.cstgroup.marketing.zl.dao.ZlGoodsDao;
import com.drag.cstgroup.marketing.zl.dao.ZlUserDao;
import com.drag.cstgroup.marketing.zl.entity.ZlGoods;
import com.drag.cstgroup.marketing.zl.entity.ZlUser;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务查询活动是否结束，每分钟跑一次
 * @author longyunbo
 *
 */
@Slf4j
@Component
public class ZlIsEndCheckTask {

	@Autowired
	ZlGoodsDao zlGoodsDao;
	@Autowired
	ZlUserDao zlUserDao;

	@Scheduled(cron = "${jobs.isEndCheckTask.schedule}")
	@Transactional
	public void find() {
		try {
			List<ZlGoods> ZlGoodsList = zlGoodsDao.findByIsEnd(0);
			for (ZlGoods zlGoods : ZlGoodsList) {
				Date endTime = zlGoods.getEndTime();
				Date nowTime = new Timestamp(System.currentTimeMillis());
				if(nowTime.after(endTime)) {
					zlGoods.setIsEnd(1);
					zlGoods.setUpdateTime(new Timestamp(System.currentTimeMillis()));
					zlGoodsDao.saveAndFlush(zlGoods);
					log.info("定时任务处理成功，更新数据{}", zlGoods);
				}
				
				int goodsId = zlGoods.getZlgoodsId();
				//查询助力中的人数
				List<ZlUser> zlList = zlUserDao.findByZlGoodsIdAndZlstatus(goodsId,ZlUser.PTSTATUS_MIDDLE);
				//助力的默认数量为1个 
				int number = 0;
				for(ZlUser ku : zlList) {
					//把在助力中的状态的修改成失败
					ku.setZlstatus(ZlUser.PTSTATUS_FAIL);
					zlUserDao.saveAndFlush(ku);
					number ++ ;
				}
				//回滚库存
				int zlgoodsNumber = zlGoods.getZlgoodsNumber();
				zlGoods.setZlgoodsNumber(zlgoodsNumber + number);
				zlGoodsDao.saveAndFlush(zlGoods);
			}
		} catch (Exception e) {
			log.error("定时异常{}", e);
			throw AMPException.getException("定时任务异常!");
		}

	}

}
