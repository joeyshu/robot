package com.fubon.robot.batch.Service;

import org.springframework.batch.core.Job;
import org.springframework.stereotype.Service;

public interface CustomJobFactory {
	
    public Job getJob();

}
