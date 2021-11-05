package com.fubon.robot.batch.robot.data;

import java.io.Serializable;
import java.util.List;

public class RobotDataDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Callinglistbot> data;

	public List<Callinglistbot> getData() {
		return data;
	}

	public void setData(List<Callinglistbot> data) {
		this.data = data;
	}
	
}
