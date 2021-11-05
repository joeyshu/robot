package com.fubon.robot.batch.robot.evdata;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * TTmActivityCallResult generated by MyEclipse Persistence Tools
 */
@Entity
@Table(name="T_TM_ACTIVITY_CALL_RESULT")
public class TTmActivityCallResult implements java.io.Serializable {
	// Fields
	/**
	 * 
	 */
	private static final long serialVersionUID = 7834378917950282989L;
	@Id
	@Column(name="RESULT_ID")
	private String resultId;
	
	@Column(name="RESULT_PID")
	private String resultPid;// 通話結果的父ID
	
	@Column(name="RESULT_CODE")
	private String resultCode;
	
	@Column(name="RESULT_NAME")
	private String resultName;
	
	@Column(name="RESULT_DESC")
	private String resultDesc;
	
	@Column(name="IS_ROOT")
	private String isRoot;

	@Column(name="IS_LOG")
	private String isLog;
	
	@Column(name="LINK_ID")
	private String linkId;
	
	@Column(name="IS_FINISHED")
	private String isFinished;
	@Column(name="IS_DELETE")
	private String isDelete;
	
	private String layer;
	
	@Column(name="RESULT_ORDER")
	private String resultOrder; 


	// ================== chaika add =================

	@Column(name="IS_CLOSED")
	private String isClosed; // 是否結案
	@Column(name="IS_BLACK")
	private String isBlack; // 禁呼名單
	@Column(name="IS_DELIVERY")
	private String isDelivery; // 是否送件
	@Column(name="INCLUDED_CONTACT_PERCENTAGE")
	private String includedContactPercentage; // 納入聯絡率
	@Column(name="INCLUDED_SUCCESS_PERCENTAGE")
	private String includedSuccessPercentage; // 維入成功率
	public String getResultId() {
		return resultId;
	}
	public void setResultId(String resultId) {
		this.resultId = resultId;
	}
	public String getResultPid() {
		return resultPid;
	}
	public void setResultPid(String resultPid) {
		this.resultPid = resultPid;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultName() {
		return resultName;
	}
	public void setResultName(String resultName) {
		this.resultName = resultName;
	}
	public String getResultDesc() {
		return resultDesc;
	}
	public void setResultDesc(String resultDesc) {
		this.resultDesc = resultDesc;
	}
	public String getIsRoot() {
		return isRoot;
	}
	public void setIsRoot(String isRoot) {
		this.isRoot = isRoot;
	}
	public String getIsLog() {
		return isLog;
	}
	public void setIsLog(String isLog) {
		this.isLog = isLog;
	}
	public String getLinkId() {
		return linkId;
	}
	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}
	public String getIsFinished() {
		return isFinished;
	}
	public void setIsFinished(String isFinished) {
		this.isFinished = isFinished;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getLayer() {
		return layer;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
	public String getResultOrder() {
		return resultOrder;
	}
	public void setResultOrder(String resultOrder) {
		this.resultOrder = resultOrder;
	}
	public String getIsClosed() {
		return isClosed;
	}
	public void setIsClosed(String isClosed) {
		this.isClosed = isClosed;
	}
	public String getIsBlack() {
		return isBlack;
	}
	public void setIsBlack(String isBlack) {
		this.isBlack = isBlack;
	}
	public String getIsDelivery() {
		return isDelivery;
	}
	public void setIsDelivery(String isDelivery) {
		this.isDelivery = isDelivery;
	}
	public String getIncludedContactPercentage() {
		return includedContactPercentage;
	}
	public void setIncludedContactPercentage(String includedContactPercentage) {
		this.includedContactPercentage = includedContactPercentage;
	}
	public String getIncludedSuccessPercentage() {
		return includedSuccessPercentage;
	}
	public void setIncludedSuccessPercentage(String includedSuccessPercentage) {
		this.includedSuccessPercentage = includedSuccessPercentage;
	}


}
