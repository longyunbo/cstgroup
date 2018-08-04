package com.drag.cstgroup.scoremall.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.drag.cstgroup.scoremall.entity.OrderDetail;


public interface OrderDetailDao extends JpaRepository<OrderDetail, String>, JpaSpecificationExecutor<OrderDetail> {
	
	
}
