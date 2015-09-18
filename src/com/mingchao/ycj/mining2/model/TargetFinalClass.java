package com.mingchao.ycj.mining2.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TargetFinalClass {
	private String uid;
	private String mid;
	private HashMap<Integer, Double> forwardClassCount = new HashMap<Integer,Double>(); 
	private HashMap<Integer, Double> commentClassCount = new HashMap<Integer,Double>(); 
	private HashMap<Integer, Double> likeClassCount = new HashMap<Integer,Double>();
	

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

	public HashMap<Integer, Double> getForwardClassCount() {
		return forwardClassCount;
	}

	public void setForwardClassCount(HashMap<Integer, Double> forwardClassCount) {
		this.forwardClassCount = forwardClassCount;
	}

	public HashMap<Integer, Double> getCommentClassCount() {
		return commentClassCount;
	}

	public void setCommentClassCount(HashMap<Integer, Double> commentClassCount) {
		this.commentClassCount = commentClassCount;
	}

	public HashMap<Integer, Double> getLikeClassCount() {
		return likeClassCount;
	}

	public void setLikeClassCount(HashMap<Integer, Double> likeClassCount) {
		this.likeClassCount = likeClassCount;
	}

	public void addForward(Integer c, Double d) {
		add(forwardClassCount, c,d);
	}

	public void addComment(Integer c, Double d) {
		add(commentClassCount, c,d);
	}

	public void addLike(Integer c, Double d) {
		add(likeClassCount, c,d);
	}

	private void add(HashMap<Integer, Double> map, Integer c, Double d) {
		Double old = map.getOrDefault(c, 0.0);
		map.put(c, old + d);
	}
	
	public Integer getForward(){
		return getMaxClass(forwardClassCount);
	}
	
	public Integer getComent(){
		return getMaxClass(commentClassCount);
	}

	public Integer getLike(){
		return getMaxClass(likeClassCount);
	}
	
	private Integer getMaxClass(HashMap<Integer, Double> map){
		Integer maxClass = 0;
		Double maxWeight = -Double.MAX_VALUE;
		for (Iterator<Map.Entry<Integer, Double>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Map.Entry<Integer, Double> e = iterator.next();
			if(e.getValue() > maxWeight){
				maxWeight = e.getValue(); 
				maxClass = e.getKey();
			}
		}
		return maxClass;
	}
}
