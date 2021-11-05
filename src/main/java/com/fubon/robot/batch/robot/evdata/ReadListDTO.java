package com.fubon.robot.batch.robot.evdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadListDTO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private List<TBusCustomer> data= new ArrayList<TBusCustomer>();

	public List<TBusCustomer> getData() {
		return data;
	}

	public void setData(List<TBusCustomer> data) {
		this.data = data;
	}
	
	
	
}
