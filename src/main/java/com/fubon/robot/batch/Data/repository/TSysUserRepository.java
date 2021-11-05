package com.fubon.robot.batch.Data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fubon.robot.batch.robot.evdata.TSysUser;
@Repository
public interface TSysUserRepository extends JpaRepository<TSysUser, String> {
	public List<TSysUser> findByUserCodeIn(List<String> userCode);
}
 