package com.fubon.robot.batch.robot.evdata;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="T_SYS_VARIABLE")
public class TSysVariable implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="variable_Id")
	private String variableId;
	@Column(name="MODULE_ID")
	private String moduleId;
	@Column(name="VARIABLE_NAME")
	private String variableName;
	@Column(name="VARIABLE_KEY")
	private String variableKey;
	@Column(name="VARIABLE_VALUE")
	private String variableValue;
	@Column(name="VARIABLE_DESC")
	private String variableDesc;
	@Column(name="VARIABLE_TYPE")
	private String variableType;
	@Column(name="IS_SPECIAL")
	private String isSpecial;
	@Column(name="IS_DEFAULT")
	private String isDefault;
	@Column(name="IS_DELETE")
	private String isDelete;
	private String type;
	@Column(name="DOC_ID")
	private String docId;
	@Column(name="IS_PASSWORD")
	private String isPassword;
	

	public String getVariableId() {
		return variableId;
	}
	public void setVariableId(String variableId) {
		this.variableId = variableId;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public String getVariableKey() {
		return variableKey;
	}
	public void setVariableKey(String variableKey) {
		this.variableKey = variableKey;
	}
	public String getVariableValue() {
		return variableValue;
	}
	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}
	public String getVariableDesc() {
		return variableDesc;
	}
	public void setVariableDesc(String variableDesc) {
		this.variableDesc = variableDesc;
	}
	public String getVariableType() {
		return variableType;
	}
	public void setVariableType(String variableType) {
		this.variableType = variableType;
	}
	public String getIsSpecial() {
		return isSpecial;
	}
	public void setIsSpecial(String isSpecial) {
		this.isSpecial = isSpecial;
	}
	public String getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(String isDefault) {
		this.isDefault = isDefault;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getIsPassword() {
		return isPassword;
	}
	public void setIsPassword(String isPassword) {
		this.isPassword = isPassword;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	
	
}
