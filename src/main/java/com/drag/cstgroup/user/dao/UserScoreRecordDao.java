package com.drag.cstgroup.user.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.user.entity.UserScoreRecord;


public interface UserScoreRecordDao extends JpaRepository<UserScoreRecord, String>, JpaSpecificationExecutor<UserScoreRecord> {
	
	
	@Query(value = "select * from t_user_drag_record where uid = ?1", nativeQuery = true)
	UserScoreRecord findGoodsDetail(int uid);
	
	
	@Query(value = "select * from t_user_drag_record where uid = ?1 and type = ?2", nativeQuery = true)
	List<UserScoreRecord> findByUidAndType(int uid,String type);
	
	@Query(value = "select * from t_user_drag_record where uid = ?1", nativeQuery = true)
	List<UserScoreRecord> findByUid(int uid);
	
}
