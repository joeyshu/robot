package com.fubon.robot.batch.robot.data;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="OB_TM_AI")
public class Callinglistbot {
	
	@Column(name="RECORD_ID")
	private Long recordId; // 記錄代號
	@Column(name="CONTACT_INFO")
	private String contactInfo; // 電話號碼
	@Column(name="CONTACT_INFO_TYPE")
	private Long contactInfoType; // 電話型式
	
	@Column(name="RECORD_TYPE")
	private Long recordType; // 記錄格式
	@Column(name="RECORD_STATUS")
	private Long recordStatus; // 記錄狀態
	@Column(name="CALL_RESULT")
	private Long callResult; // 撥打結果
	
	private Long attempt; // 撥打次數
	@Column(name="DIAL_SCHED_TIME")
	private Long dialSchedTime; // 撥打排程時間
	@Column(name="CALL_TIME")
	private Long callTime; // 撥打時間
	@Column(name="DAILY_FROM")
	private Long dailyFrom; // 當天起始可撥打時間
	@Column(name="DAILY_TILL")
	private Long dailyTill; // 當天結束可撥打時間
	@Column(name="TZ_DBID")
	private Long tzDbid; // 時區
	@Column(name ="CAMPAIGN_ID")
	private Long campaignid; // OCS活動編號
	@Column(name ="AGENT_ID")
	private String agentId; // 座席編號
	@Id
	@Column(name ="CHAIN_ID")
	private Long chainId; // 記錄鏈編號
	
	@Column(name ="chain_N")
	private Long chainN;// 記錄鏈順序號
	@Column(name ="GROUP_ID")
	private Long groupId;// 群組編號
	@Column(name ="APP_ID")
	private Long appId; // Application編號

	private String treatments; // 租戶
	
	@Column(name ="MEDIA_REF")
	private Long mediaRef;// 媒體
	@Column(name ="EMAIL_SUBJECT")
	private String emailSubject;// E-Mail 標題
	@Column(name ="EMAIL_TEMPLATE_ID")
	private Long emailTemplateId;// E-Mail模版編號
	@Column(name ="SWITCH_ID")
	private Long switchId; // SIP Server Switch編號

	private String campaignname;//活動名稱

	private String gend;// 姓別

	private String custname;// 客戶姓名

	private String mp;// 手機號碼

	
	private String projectDetail; // 專案細項
	
	@Column(name ="ACTION_RESULT")
	private String actionResult;// 小I回覆結果

	private String calluuid;// Genesys CallUUID
	
	
	
	public String getProjectDetail() {
		return projectDetail;
	}

	public void setProjectDetail(String projectDetail) {
		this.projectDetail = projectDetail;
	}

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

	public Long getAttempt() {
		return attempt;
	}

	public void setAttempt(Long attempt) {
		this.attempt = attempt;
	}

	public Long getDialSchedTime() {
		return dialSchedTime;
	}

	public void setDialSchedTime(Long dialSchedTime) {
		this.dialSchedTime = dialSchedTime;
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


	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
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

	public Long getMediaRef() {
		return mediaRef;
	}

	public void setMediaRef(Long mediaRef) {
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

	
	public Long getCampaignid() {
		return campaignid;
	}

	public void setCampaignid(Long campaignid) {
		this.campaignid = campaignid;
	}


	public String getGend() {
		return gend;
	}

	public void setGend(String gend) {
		this.gend = gend;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getMp() {
		return mp;
	}

	public void setMp(String mp) {
		this.mp = mp;
	}


	public String getActionResult() {
		return actionResult;
	}

	public void setActionResult(String actionResult) {
		this.actionResult = actionResult;
	}

	public String getCalluuid() {
		return calluuid;
	}

	public void setCalluuid(String calluuid) {
		this.calluuid = calluuid;
	}

	public String getCampaignname() {
		return campaignname;
	}

	public void setCampaignname(String campaignname) {
		this.campaignname = campaignname;
	}
	
	
}
