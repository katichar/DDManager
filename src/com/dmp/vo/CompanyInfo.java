package com.dmp.vo;

public class CompanyInfo {
	private String code="-1";
	
	private String nameCN;
	private String nameEN;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getNameCN() {
		if (nameCN==null){
			nameCN="";
		}
		return nameCN;
	}
	public void setNameCN(String nameCN) {
		this.nameCN = nameCN;
	}
	public String getNameEN() {
		return nameEN;
	}
	public void setNameEN(String nameEN) {
		this.nameEN = nameEN;
	}
}
