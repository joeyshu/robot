package com.fubon.robot.batch.writer;

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.robot.evdata.ReadListDTO;
import com.fubon.robot.batch.robot.evdata.TBusCustomer;

@Component
@StepScope
@Transactional
public class WhiteListWriter extends StepExecutionListenerSupport implements ItemWriter<TBusCustomer> {

	private StepExecution stepExecution;

	@Override
	public void beforeStep(StepExecution stepExecution) {
		super.beforeStep(stepExecution);
		this.stepExecution = stepExecution;
	}

	@Override
	public void write(List<? extends TBusCustomer> items) throws Exception {
		// TODO Auto-generated method stub
		ReadListDTO dto = (ReadListDTO)stepExecution.getJobExecution()
				.getExecutionContext().get(RederListJobConfig.JOB_EXECUTION_EVOICE_DATA_RESULT);
		
		if(dto == null) {
			dto = new ReadListDTO();
		}
		
		dto.getData().addAll(items);
		
		stepExecution.getJobExecution().getExecutionContext()
			.put(RederListJobConfig.JOB_EXECUTION_EVOICE_DATA_RESULT, dto);
		
		
	}

}
