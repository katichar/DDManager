package com.dmp.vo;

public class TranCompInfo {
	public static String [] titles=new String[]{"编码","新企业名称","旧企业名称","操作类型"};
	private String code;
	private String name;
	private CompanyInfo comp;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CompanyInfo getComp() {
		if (comp==null){
			comp = new CompanyInfo();
		}
		return comp;
	}
	public void setComp(CompanyInfo comp) {
		this.comp = comp;
	}
	
}
