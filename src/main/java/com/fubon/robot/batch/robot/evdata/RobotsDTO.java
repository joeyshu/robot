package com.fubon.robot.batch.robot.evdata;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class RobotsDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<TSysUser> robotDatas;
	
	public RobotsDTO(List<TSysUser> robotUsers) {
		this.robotDatas = robotUsers;
	}
	
	public List<TSysUser> getRobotDatas() {
		return robotDatas;
	}
	
}
