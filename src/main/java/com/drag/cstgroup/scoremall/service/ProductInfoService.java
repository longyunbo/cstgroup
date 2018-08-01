package com.drag.cstgroup.scoremall.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.drag.cstgroup.scoremall.dao.ProductInfoDao;
import com.drag.cstgroup.scoremall.entity.ProductInfo;
import com.drag.cstgroup.scoremall.vo.ProductInfoVo;
import com.drag.cstgroup.utils.BeanUtils;
import com.drag.cstgroup.utils.DateUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductInfoService {

	@Autowired
	private ProductInfoDao productDao;

	/**
	 * 查询商品列表
	 * @return
	 */
	public List<ProductInfoVo> listGoods(String type) {
		List<ProductInfoVo> goodsResp = new ArrayList<ProductInfoVo>();
		List<ProductInfo> goodsList = productDao.findByType(type);
		if (goodsList != null && goodsList.size() > 0) {
			for (ProductInfo goods : goodsList) {
				ProductInfoVo resp = new ProductInfoVo();
				this.copyProperties(goods, resp);
				goodsResp.add(resp);
			}
		}
		return goodsResp;
	}
	
	/**
	 * 查询商品详情
	 * @return
	 */
	public ProductInfoVo goodsDetail(int goodsId) {
		ProductInfoVo detailVo = new ProductInfoVo();
		ProductInfo goods = productDao.findGoodsDetail(goodsId);
		if(goods != null) {
			this.copyProperties(goods, detailVo);
		}
		return detailVo;
	}
	
	
	/**
	 * 查询活动是否结束
	 * @param goodsId
	 * @return
	 */
	public Boolean checkEnd(int goodsId) {
		boolean endFlag = false;
		ProductInfo goods = productDao.findGoodsDetail(goodsId);
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
	 * 商品减库存
	 * @param goods
	 * @param number
	 * @return
	 */
	public Boolean delStock(ProductInfo goods, int number) {
		boolean flag = false;
		int goodsNumber = goods.getGoodsNumber();
		if (goodsNumber - number < 0) {
			// 库存不足
			flag = false;
		} else {
			flag = true;
			int nowGoodsNum = goodsNumber - number;
			goods.setGoodsNumber(nowGoodsNum);
			productDao.saveAndFlush(goods);
		}
		return flag;
	}
	
	/**
	 * 增加购买次数
	 * @param goods
	 * @param number
	 */
	public void addSuccTimes(ProductInfo goods) {
		int succTime = goods.getSuccTimes();
		goods.setSuccTimes(succTime + 1);
		productDao.saveAndFlush(goods);
	}
	
	/**
	 * 提取copy方法
	 * @param goods
	 * @param detailVo
	 */
	public void copyProperties(ProductInfo goods,ProductInfoVo detailVo) {
		BeanUtils.copyProperties(goods, detailVo,new String[]{"createTime", "updateTime","startTime","endTime"});
		detailVo.setCreateTime((DateUtil.format(goods.getCreateTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setUpdateTime((DateUtil.format(goods.getUpdateTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setStartTime((DateUtil.format(goods.getStartTime(), "yyyy-MM-dd HH:mm:ss")));
		detailVo.setEndTime((DateUtil.format(goods.getEndTime(), "yyyy-MM-dd HH:mm:ss")));
	}
	
}
