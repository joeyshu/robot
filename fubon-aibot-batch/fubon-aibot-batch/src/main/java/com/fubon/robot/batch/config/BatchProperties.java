package com.fubon.robot.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch-properties")
public class BatchProperties {
	
	private String systemPropPath;

	public String getSystemPropPath() {
		return systemPropPath;
	}

	public void setSystemPropPath(String systemPropPath) {
		this.systemPropPath = systemPropPath;
	}
}
