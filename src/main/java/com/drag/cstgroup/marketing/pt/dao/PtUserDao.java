package com.drag.cstgroup.marketing.pt.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.drag.cstgroup.marketing.pt.entity.PtUser;


public interface PtUserDao extends JpaRepository<PtUser, String>, JpaSpecificationExecutor<PtUser> {
	
	
	@Query(value = "select * from pt_user where ptgoods_id = ?1", nativeQuery = true)
	List<PtUser> findByPtGoodsId(String goodsId);
	
	@Query(value = "select * from pt_user where ptgoods_id = ?1 and ptstatus = ?2", nativeQuery = true)
	List<PtUser> findByPtGoodsIdAndStatus(int goodsId,int status);
	
	@Query(value = "select * from pt_user where ptgoods_id = ?1 and is_header = ?2 order by create_time desc", nativeQuery = true)
	List<PtUser> findByPtGoodsIdAndIsHead(int goodsId,int isHead);
	
	@Query(value = "select * from pt_user where uid = ?1 order by create_time desc", nativeQuery = true)
	List<PtUser> findByUid(int uid);
	
	@Query(value = "select * from pt_user where uid = ?1 and ptgoods_id = ?2 and is_header = ?3 and ptstatus = ?4 order by create_time desc", nativeQuery = true)
	List<PtUser> findByUidAndPtgoodsIdAndIsHeadAndPtstatus(int uid,int goodsId,int isHead,int ptstatus);
	
	@Query(value = "select * from pt_user where ptcode = ?1 order by create_time desc", nativeQuery = true)
	List<PtUser> findByPtCode(String ptCode);
	
	@Query(value = "select * from pt_user where ptgoods_id = ?1 and is_header = ?2 and ptcode = ?3 order by create_time desc", nativeQuery = true)
	List<PtUser> findByPtGoodsIdAndIsHeadAndPtCode(int goodsId,int isHead,String ptCode);
	
	@Query(value = "select * from pt_user where ptgoods_id = ?1 and uid = ?2 and ptcode = ?3", nativeQuery = true)
	PtUser findByPtGoodsIdAndUidAndPtCode(int goodsId,int uid,String ptCode);
	
	@Modifying
	@Query(value = "UPDATE pt_user set ptstatus = 2 where ptcode =?1",nativeQuery = true)
	public int updatePtstatus(String ptcode);
}
