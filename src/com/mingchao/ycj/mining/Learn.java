package com.mingchao.ycj.mining;

import java.util.HashMap;

public class Learn {
	private HashMap<Integer,HashMap<String,Integer>> mClsMUidCount;
	private HashMap<Integer,HashMap<String,Integer>> mClsMWordCount;
	private HashMap<Integer,Integer> mClsCount;
	private int nRecords;
	
	public Learn() {
		super();
	}

	public Learn(HashMap<Integer, HashMap<String, Integer>> mClsMUidCount,
			HashMap<Integer, HashMap<String, Integer>> mClsMWordCount,
			HashMap<Integer, Integer> mClsCount, int nRecords) {
		super();
		this.mClsMUidCount = mClsMUidCount;
		this.mClsMWordCount = mClsMWordCount;
		this.mClsCount = mClsCount;
		this.nRecords = nRecords;
	}

	public int pred(String uid, String [] wordArray){
		double target = -Double.MAX_VALUE;
		int targetCls = 0;
		for(Integer cls : mClsCount.keySet()){
			double t = cweight(cls,uid,wordArray);
			if(t > target){
				target = t;
				targetCls = cls; 
			}
		}
		System.out.print("Target Class: "+ targetCls +"\n");
		return targetCls;
	}

	private  double cweight(Integer cls,String uid, String [] wordList){
//		System.out.print("Class: "+cls+"\t");
		double weight = 0.0;
		int clsCount = get(mClsCount, cls);
		double classWeight = Math.log((double)clsCount/nRecords);
		weight += classWeight;
//		System.out.print("weight: "+weight+"\t");
		int uidCount = get(mClsMUidCount, cls,uid);
		double uidWeight =  Math.log((uidCount+1.0)/(clsCount+clsCount));
		weight += uidWeight;
//		System.out.print(weight+"\t");
		double wordWeight = 0.0;
		for(String word:wordList){
			int wordCount = get(mClsMWordCount, cls, word);
			wordWeight += Math.log((wordCount+1.0)/(clsCount+clsCount));
		}
		wordWeight /= wordList.length;
		weight += wordWeight;
//		System.out.print(weight+"\t");
//		System.out.println("classWeight: "+ classWeight + "\t" + "uidWeight: " + uidWeight+"\t" + "wordWeight: " + wordWeight);
		return weight;
	}
	
	private int get(HashMap<Integer,Integer> m,Integer key){
		return m.getOrDefault(key, 0);
	}
	private int get(HashMap<Integer,HashMap<String,Integer>> m,Integer key1,String key2){
		HashMap<String, Integer> m2 = m.get(key1);
		if(m2 != null){
			return (int) m2.getOrDefault(key2, 0);
		}else{
			return 0;
		}
	}
	public void setmClsMUidCount(HashMap<Integer, HashMap<String, Integer>> mClsMUidCount) {
		this.mClsMUidCount = mClsMUidCount;
	}

	public void setmClsMWordCount(HashMap<Integer, HashMap<String, Integer>> mClsMWordCount) {
		this.mClsMWordCount = mClsMWordCount;
	}

	public void setmClsCount(HashMap<Integer, Integer> mClsCount) {
		this.mClsCount = mClsCount;
	}

	public void setnRecords(int nRecords) {
		this.nRecords = nRecords;
	}
}
