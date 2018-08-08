package com.drag.cstgroup.pay.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.drag.cstgroup.pay.entity.UserRechargeRecord;


public interface RechargeRecordDao extends JpaRepository<UserRechargeRecord, String>, JpaSpecificationExecutor<UserRechargeRecord> {
	
	
}
