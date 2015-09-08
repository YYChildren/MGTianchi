package com.mingchao.ycj.mining2.model;

public class TargetClass implements Comparable<TargetClass>{
	private String uid;
	private String mid;
	private Integer targetClass;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public Integer getTargetClass() {
		return targetClass;
	}
	public void setTargetClass(Integer targetClass) {
		this.targetClass = targetClass;
	}
	@Override
	public int compareTo(TargetClass o) {
		int r = 0;
		if( (r = this.uid.compareTo(o.uid)) == 0){
			r = this.mid.compareTo(o.mid);
		}
		return r;			
	}
}
