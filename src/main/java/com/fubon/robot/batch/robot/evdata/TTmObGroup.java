package com.fubon.robot.batch.robot.evdata;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="T_TM_OBGROUP")
public class TTmObGroup implements java.io.Serializable {
	private static final long serialVersionUID = -4929828757351055691L;
	@Id
	@Column(name="OBGROUP_ID")
	private String obGroupId;
	@Column(name="ACTIVITY_VARIABLE_KEY")
	private String activityVariableKey;
	@Column(name="OBGROUP_NO")
	private String obGroupNo;
	@Column(name="OBGROUP_DESC")
	private String obGroupDesc;
	@Column(name="ACTIVITY_DESC")
	private String activityDesc;
	@Column(name="ACTIVITY_BETWEEN")
	private String activityBetween;
	@Column(name="IS_DELETE")
	private String isDelete;

	// ///
	// Constructors

	/** default constructor */
	public TTmObGroup() {
	}

	/** minimal constructor */
	public TTmObGroup(String obGroupId, String obGroupNo) {
		this.obGroupId = obGroupId;
		this.obGroupNo = obGroupNo;
	}

	// Property accessors

	public TTmObGroup(String obGroupId, String activityVariableKey, String obGroupNo, String obGroupDesc, String activityDesc, String activityBetween) {
		super();
		this.obGroupId = obGroupId;
		this.activityVariableKey = activityVariableKey;
		this.obGroupNo = obGroupNo;
		this.obGroupDesc = obGroupDesc;
		this.activityDesc = activityDesc;
		this.activityBetween = activityBetween;

	}

	public String getObGroupId() {
		return obGroupId;
	}

	public void setObGroupId(String obGroupId) {
		this.obGroupId = obGroupId;
	}

	public String getActivityVariableKey() {
		return activityVariableKey;
	}

	public void setActivityVariableKey(String activityVariableKey) {
		this.activityVariableKey = activityVariableKey;
	}

	public String getObGroupNo() {
		return obGroupNo;
	}

	public void setObGroupNo(String obGroupNo) {
		this.obGroupNo = obGroupNo;
	}

	public String getObGroupDesc() {
		return obGroupDesc;
	}

	public void setObGroupDesc(String obGroupDesc) {
		this.obGroupDesc = obGroupDesc;
	}

	public String getActivityDesc() {
		return activityDesc;
	}

	public void setActivityDesc(String activityDesc) {
		this.activityDesc = activityDesc;
	}

	public String getActivityBetween() {
		return activityBetween;
	}

	public void setActivityBetween(String activityBetween) {
		this.activityBetween = activityBetween;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
}
