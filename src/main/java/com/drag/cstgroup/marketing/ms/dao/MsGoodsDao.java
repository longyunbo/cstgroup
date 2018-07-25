package com.drag.cstgroup.marketing.ms.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.marketing.ms.entity.MsGoods;


public interface MsGoodsDao extends JpaRepository<MsGoods, String>, JpaSpecificationExecutor<MsGoods> {
	
	List<MsGoods> findByIsEnd(int isEnd);
	
	@Query(value = "select * from ms_goods where msgoods_id = ?1", nativeQuery = true)
	MsGoods findGoodsDetail(int goodsId);
	
	
}
