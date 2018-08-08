package com.drag.cstgroup.user.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.drag.cstgroup.user.entity.UserProfession;

public interface UserProfessionDao extends JpaRepository<UserProfession, String>, JpaSpecificationExecutor<UserProfession> {

	
}
