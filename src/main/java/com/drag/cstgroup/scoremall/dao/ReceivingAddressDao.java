package com.drag.cstgroup.scoremall.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.scoremall.entity.ReceivingAddress;


public interface ReceivingAddressDao extends JpaRepository<ReceivingAddress, String>, JpaSpecificationExecutor<ReceivingAddress> {
	
	
	@Query(value = "select * from t_receiving_address where openid = ?1", nativeQuery = true)
	List<ReceivingAddress> findByOpenid(String openid);
	
}
