package com.fubon.robot.batch.robot.evdata;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="T_BUS_BLACK_LIST")
public class Blacklist implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="black_Id")
	private String blackId;
	
	
	@Column(name="CUSTOMER_NAME")
	private String customerName; // 客戶名稱
	@Column(name="PHONE_NUMBER")
	private String phoneNumber; // 電話號碼, customer148
	private String status; // 狀態  1 己報送   2審核通過   3 審核不通過   4複核通過    5複核不通過    6,7己回送
	@Column(name="IDENTITY_ID")
	private String identityId; // 身份證字號 , customer28
	@Column(name="LOOPBACK_MARK")
	private String loopbackMark;
	@Column(name="OFFER_MAN")
	private String offerMan;
	@Column(name="OFFER_TIME")
	private Date offerTime;
	@Column(name="TRIAL_MAN")
	private String trialMan;
	@Column(name="REVIEW_MAN")
	private String reviewMan;
	@Column(name="REASON")
	private String reason; // 理由
	@Column(name="SUBMIT_MAN")
	private String submitMan;
	@Column(name="SUBMIT_TIME")
	private Date submitTime;
	@Column(name="DELETE_MARK")
	private String deleteMark; // 是否刪除
	@Column(name="ORGANIZE_NAME")
	private String organizeName;// 報送部門

	public String getOrganizeName() {
		return organizeName;
	}

	public void setOrganizeName(String organizeName) {
		this.organizeName = organizeName;
	}
	public String getBlackId() {
		return this.blackId;
	}

	public void setBlackId(String blackId) {
		this.blackId = blackId;
	}

	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getIdentityId() {
		return this.identityId;
	}

	public void setIdentityId(String identityId) {
		this.identityId = identityId;
	}

	public String getLoopbackMark() {
		return this.loopbackMark;
	}

	public void setLoopbackMark(String loopbackMark) {
		this.loopbackMark = loopbackMark;
	}

	public String getOfferMan() {
		return this.offerMan;
	}

	public void setOfferMan(String offerMan) {
		this.offerMan = offerMan;
	}

	public Date getOfferTime() {
		return this.offerTime;
	}

	public void setOfferTime(Date offerTime) {
		this.offerTime = offerTime;
	}

	public String getTrialMan() {
		return this.trialMan;
	}

	public void setTrialMan(String trialMan) {
		this.trialMan = trialMan;
	}

	public String getReviewMan() {
		return this.reviewMan;
	}

	public void setReviewMan(String reviewMan) {
		this.reviewMan = reviewMan;
	}

	public String getReason() {
		return this.reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getSubmitMan() {
		return this.submitMan;
	}

	public void setSubmitMan(String submitMan) {
		this.submitMan = submitMan;
	}

	public Date getSubmitTime() {
		return this.submitTime;
	}

	public void setSubmitTime(Date submitTime) {
		this.submitTime = submitTime;
	}

	public String getDeleteMark() {
		return this.deleteMark;
	}

	public void setDeleteMark(String deleteMark) {
		this.deleteMark = deleteMark;
	}
}
