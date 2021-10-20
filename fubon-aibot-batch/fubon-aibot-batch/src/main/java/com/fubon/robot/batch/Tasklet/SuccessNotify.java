package com.fubon.robot.batch.Tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fubon.robot.batch.SampleBatchApplication;
import com.fubon.robot.batch.batch.SysSeting;
import com.fubon.robot.batch.batch.SystemFileSettinService;
import com.fubon.robot.batch.config.RederListJobConfig;
import com.fubon.robot.batch.config.WriteListToRobtConfig;
import com.fubon.robot.batch.thread.post.DateUtils;
import com.fubon.robot.batch.thread.post.MailSendBox;
import com.fubon.robot.batch.writer.RobotWriter;


@Component
@StepScope
@Transactional
public class SuccessNotify implements Tasklet{

	@Autowired
	SystemFileSettinService systemFileSettinService;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		//流程上最後寫入筆數
		Object successCount = chunkContext.getStepContext().getStepExecution().getJobExecution()
		.getExecutionContext().get(RobotWriter.successCount);
		
		//批次B抓出筆數
		Object jobdataSIZE = chunkContext.getStepContext().getStepExecution().getJobExecution()
		.getExecutionContext().get(WriteListToRobtConfig.JOB_B_ROBOT_DATA_SIZE);
		
		//批次名稱
		String batchName = (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext().get(SampleBatchApplication.JOB_MAIL_NAME);
		//批次開始時間
		String batchStartTime = (String) chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getExecutionContext().get(SampleBatchApplication.JOB_START_TIME);

		
		//1 取得 相關MAIL 資訊從設定檔
		SysSeting sysSeting = systemFileSettinService.getSysFileSetting();
		//2寄送人位置
		String[] sendUserMailAddress = sysSeting.getMailAddrUser().split(",");
		
		for(String senduser: sendUserMailAddress) {
			MailSendBox box = new MailSendBox(senduser, "robotUser", 
					sysSeting.getMailServerAddress(),
					sysSeting.getMailServerPort(),
					"smtp",
					"匯入機器人批次 於"+DateUtils.getNowTime()+"結束",
					"管理員您好!!\r\n"
					+"電銷系統電銷系統整合AI機器人 結束"+ batchName +"執行成功\r\n"
					+"開始時間:" +batchStartTime +"\r\n 結束時間" + DateUtils.getNowTime()
					+"\r\n 寫入AI機器人資料庫 "+(successCount==null?"":successCount) +(jobdataSIZE==null?"":jobdataSIZE) +"筆",
					sysSeting.getMailSendUser(),
					sysSeting.getMailSendPwd());
			box.sendMail();
		}
		return RepeatStatus.FINISHED;
	}

}
