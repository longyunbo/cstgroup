package com.drag.cstgroup.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.user.entity.UserScoreUsedRecord;


public interface UserScoreUsedRecordDao extends JpaRepository<UserScoreUsedRecord, String>, JpaSpecificationExecutor<UserScoreUsedRecord> {
	
	@Query(value = "select * from t_user_drag_used_record where uid = ?1 and type = ?2", nativeQuery = true)
	List<UserScoreUsedRecord> findByUidAndType(int uid,String type);
	
	@Query(value = "select * from t_user_drag_used_record where uid = ?1", nativeQuery = true)
	List<UserScoreUsedRecord> findByUid(int uid);
	
	
}
