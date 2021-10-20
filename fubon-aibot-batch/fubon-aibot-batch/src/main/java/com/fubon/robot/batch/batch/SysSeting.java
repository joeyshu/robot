package com.fubon.robot.batch.batch;

public class SysSeting {
	
	private Integer rowQuantity;//撈取名單最大筆數
	
	private Integer dedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans;//重複週期 Day 數
	
	private Integer dedupingOutboundByAibotInDays;
	
	private String  mailAddrIt; //錯誤訊息 寄送給It mail 
	
	private String  robotUserNumber; // 機器人員編設定 
	
	private String 	mailAddrUser; //成功總結訊息 寄送給USER
	
	private String mailServerAddress; //郵件伺服器位址
	
	private String mailServerPort; //郵件伺服器位址
	
	private String mailSendUser; // 寄送郵件的帳號
	
	private String mailSendPwd; // 寄送郵件的帳號
	
	private String aibotDBServerAddress;
	
	private String aibotDBServerPort;
	
	private String aibotDBServerAccount;
	
	private String aibotDBServerPwd;
	
	private Integer numberOfThreads;
	
	public String getMailServerPort() {
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	public String getMailServerAddress() {
		return mailServerAddress;
	}

	public void setMailServerAddress(String mailServerAddress) {
		this.mailServerAddress = mailServerAddress;
	}

	public String getMailSendUser() {
		return mailSendUser;
	}

	public void setMailSendUser(String mailSendUser) {
		this.mailSendUser = mailSendUser;
	}

	public String getMailSendPwd() {
		return mailSendPwd;
	}

	public void setMailSendPwd(String mailSendPwd) {
		this.mailSendPwd = mailSendPwd;
	}

	public Integer getRowQuantity() {
		return rowQuantity;
	}

	public void setRowQuantity(Integer rowQuantity) {
		this.rowQuantity = rowQuantity;
	}


	public Integer getDedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans() {
		return dedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans;
	}

	public void setDedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans(
			Integer dedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans) {
		this.dedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans = dedupingOutboundByEventsInDaystypeOfTelemarketingOfCreditLoans;
	}
	
	public Integer getDedupingOutboundByAibotInDays() {
		return dedupingOutboundByAibotInDays;
	}

	public void setDedupingOutboundByAibotInDays(Integer dedupingOutboundByAibotInDays) {
		this.dedupingOutboundByAibotInDays = dedupingOutboundByAibotInDays;
	}

	public String getMailAddrIt() {
		return mailAddrIt;
	}

	public void setMailAddrIt(String mailAddrIt) {
		this.mailAddrIt = mailAddrIt;
	}

	public String getRobotUserNumber() {
		return robotUserNumber;
	}

	public void setRobotUserNumber(String robotUserNumber) {
		this.robotUserNumber = robotUserNumber;
	}

	public String getMailAddrUser() {
		return mailAddrUser;
	}

	public void setMailAddrUser(String mailAddrUser) {
		this.mailAddrUser = mailAddrUser;
	}

	public String getAibotDBServerAddress() {
		return aibotDBServerAddress;
	}

	public void setAibotDBServerAddress(String aibotDBServerAddress) {
		this.aibotDBServerAddress = aibotDBServerAddress;
	}

	public String getAibotDBServerPort() {
		return aibotDBServerPort;
	}

	public void setAibotDBServerPort(String aibotDBServerPort) {
		this.aibotDBServerPort = aibotDBServerPort;
	}

	public String getAibotDBServerAccount() {
		return aibotDBServerAccount;
	}

	public void setAibotDBServerAccount(String aibotDBServerAccount) {
		this.aibotDBServerAccount = aibotDBServerAccount;
	}

	public String getAibotDBServerPwd() {
		return aibotDBServerPwd;
	}

	public void setAibotDBServerPwd(String aibotDBServerPwd) {
		this.aibotDBServerPwd = aibotDBServerPwd;
	}

	public Integer getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(Integer numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}
	
}
