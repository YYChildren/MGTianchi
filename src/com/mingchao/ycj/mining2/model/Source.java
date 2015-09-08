package com.mingchao.ycj.mining2.model;

public class Source  implements Comparable<Source>,java.io.Serializable{
	private static final long serialVersionUID = 1359110339945323841L;
	protected String uid;
	protected String mid;
	protected String[] wordArray;
	
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

	public String[] getWordArray() {
		return wordArray;
	}

	public void setWordArray(String[] wordArray) {
		this.wordArray = wordArray;
	}

	@Override
	public int compareTo(Source o) {
		int r = 0;
		if ((r = this.uid.compareTo(o.uid)) == 0) {
			r = this.mid.compareTo(o.mid);
		}
		return r;
	}
}
