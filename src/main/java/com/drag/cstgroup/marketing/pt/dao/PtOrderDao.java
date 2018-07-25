package com.drag.cstgroup.marketing.pt.dao;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.marketing.pt.entity.PtOrder;


public interface PtOrderDao extends JpaRepository<PtOrder, String>, JpaSpecificationExecutor<PtOrder> {
	
	
	@Query(value = "select * from pt_order where ptcode = ?1 and is_header = ?2", nativeQuery = true)
	PtOrder findByPtCodeAndIsHeader(String ptcode,int is_header);
	
	@Query(value = "select * from pt_order where ptcode in (?1) ", nativeQuery = true)
	List<PtOrder> findByPtCodeIn(Set<String> ptcodes);
}
