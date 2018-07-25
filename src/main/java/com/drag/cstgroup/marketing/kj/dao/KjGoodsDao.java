package com.drag.cstgroup.marketing.kj.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.marketing.kj.entity.KjGoods;


public interface KjGoodsDao extends JpaRepository<KjGoods, String>, JpaSpecificationExecutor<KjGoods> {
	
	List<KjGoods> findByIsEnd(int isEnd);
	
	@Query(value = "select * from kj_goods where kjgoods_id = ?1", nativeQuery = true)
	KjGoods findGoodsDetail(int goodsId);
	
	
}
