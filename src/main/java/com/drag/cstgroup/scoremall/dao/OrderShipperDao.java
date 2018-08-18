package com.drag.cstgroup.scoremall.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.drag.cstgroup.scoremall.entity.OrderShipper;


public interface OrderShipperDao extends JpaRepository<OrderShipper, String>, JpaSpecificationExecutor<OrderShipper> {
	
	
	
}
