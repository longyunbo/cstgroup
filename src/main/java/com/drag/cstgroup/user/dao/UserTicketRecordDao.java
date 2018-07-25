package com.drag.cstgroup.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.drag.cstgroup.user.entity.UserTicketRecord;


public interface UserTicketRecordDao extends JpaRepository<UserTicketRecord, String>, JpaSpecificationExecutor<UserTicketRecord> {
	
	
	
}
