package com.drag.cstgroup.marketing.zl.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.marketing.zl.entity.ZlGoods;


public interface ZlGoodsDao extends JpaRepository<ZlGoods, String>, JpaSpecificationExecutor<ZlGoods> {
	
	List<ZlGoods> findByIsEnd(int isEnd);
	
	@Query(value = "select * from zl_goods where zlgoods_id = ?1", nativeQuery = true)
	ZlGoods findGoodsDetail(int goodsId);
	
	
}
