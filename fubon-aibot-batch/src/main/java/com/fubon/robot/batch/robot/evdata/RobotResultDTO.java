package com.fubon.robot.batch.robot.evdata;

import java.io.Serializable;

public class RobotResultDTO implements Serializable{
	
	private Long recordId; //記錄代號
	
	private String contactInfo; //電話號碼

	private Long contactInfoType; // 電話型式

	private Long recordType; // 記錄格式

	private Long recordStatus; // 記錄狀態

	private Long callResult; // 撥打結果

	private String attempt; // 撥打次數

	private Long dialSchedTimep;// 撥打排程時間

	private Long callTime; // 撥打時間

	private Long dailyFrom; // 當天起始可撥打時間

	private Long dailyTill; // 當天結束可撥打時間

	private Long tzDbid; // 時區

	private Long campaignId; // OCS活動編號

	private Long agentId; // 座席編號

	private Long chainId; // 記錄鏈編號

	private Long chainN; // 記錄鏈順序號

	private Long groupId; // 群組編號

	private Long appId; // Application編號

	private String treatments; // 租戶

	private String mediaRef; // 媒體

	private String emailSubject;// E-Mail 標題

	private Long emailTemplateId; // E-Mail模版編號

	private Long switchId; // SIP Server Switch編號

	private String CampaignName; // 活動名稱

	private String gend; // 姓別

	private String custName; // 客戶姓名

	private String projectDetail; // 專案細項

	private String actionResult; // 貼標 A1~A10

	private String callUUID; // 名單ID;

	private String ex1;
	private String ex2;
	private String ex3;
	public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public String getContactInfo() {
		return contactInfo;
	}
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}
	public Long getContactInfoType() {
		return contactInfoType;
	}
	public void setContactInfoType(Long contactInfoType) {
		this.contactInfoType = contactInfoType;
	}
	public Long getRecordType() {
		return recordType;
	}
	public void setRecordType(Long recordType) {
		this.recordType = recordType;
	}
	public Long getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(Long recordStatus) {
		this.recordStatus = recordStatus;
	}
	public Long getCallResult() {
		return callResult;
	}
	public void setCallResult(Long callResult) {
		this.callResult = callResult;
	}
	public String getAttempt() {
		return attempt;
	}
	public void setAttempt(String attempt) {
		this.attempt = attempt;
	}
	public Long getDialSchedTimep() {
		return dialSchedTimep;
	}
	public void setDialSchedTimep(Long dialSchedTimep) {
		this.dialSchedTimep = dialSchedTimep;
	}
	public Long getCallTime() {
		return callTime;
	}
	public void setCallTime(Long callTime) {
		this.callTime = callTime;
	}
	public Long getDailyFrom() {
		return dailyFrom;
	}
	public void setDailyFrom(Long dailyFrom) {
		this.dailyFrom = dailyFrom;
	}
	public Long getDailyTill() {
		return dailyTill;
	}
	public void setDailyTill(Long dailyTill) {
		this.dailyTill = dailyTill;
	}
	public Long getTzDbid() {
		return tzDbid;
	}
	public void setTzDbid(Long tzDbid) {
		this.tzDbid = tzDbid;
	}
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
	public Long getAgentId() {
		return agentId;
	}
	public void setAgentId(Long agentId) {
		this.agentId = agentId;
	}
	public Long getChainId() {
		return chainId;
	}
	public void setChainId(Long chainId) {
		this.chainId = chainId;
	}
	public Long getChainN() {
		return chainN;
	}
	public void setChainN(Long chainN) {
		this.chainN = chainN;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public Long getAppId() {
		return appId;
	}
	public void setAppId(Long appId) {
		this.appId = appId;
	}
	public String getTreatments() {
		return treatments;
	}
	public void setTreatments(String treatments) {
		this.treatments = treatments;
	}
	public String getMediaRef() {
		return mediaRef;
	}
	public void setMediaRef(String mediaRef) {
		this.mediaRef = mediaRef;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public Long getEmailTemplateId() {
		return emailTemplateId;
	}
	public void setEmailTemplateId(Long emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}
	public Long getSwitchId() {
		return switchId;
	}
	public void setSwitchId(Long switchId) {
		this.switchId = switchId;
	}
	public String getCampaignName() {
		return CampaignName;
	}
	public void setCampaignName(String campaignName) {
		CampaignName = campaignName;
	}
	public String getGend() {
		return gend;
	}
	public void setGend(String gend) {
		this.gend = gend;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getProjectDetail() {
		return projectDetail;
	}
	public void setProjectDetail(String projectDetail) {
		this.projectDetail = projectDetail;
	}
	public String getActionResult() {
		return actionResult;
	}
	public void setActionResult(String actionResult) {
		this.actionResult = actionResult;
	}
	public String getCallUUID() {
		return callUUID;
	}
	public void setCallUUID(String callUUID) {
		this.callUUID = callUUID;
	}
	public String getEx1() {
		return ex1;
	}
	public void setEx1(String ex1) {
		this.ex1 = ex1;
	}
	public String getEx2() {
		return ex2;
	}
	public void setEx2(String ex2) {
		this.ex2 = ex2;
	}
	public String getEx3() {
		return ex3;
	}
	public void setEx3(String ex3) {
		this.ex3 = ex3;
	}



}
