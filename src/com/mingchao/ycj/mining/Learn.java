package com.mingchao.ycj.mining;

import java.util.HashMap;

public class Learn {
	private HashMap<Integer, HashMap<String, Double>> mClsMUidCount;
	private HashMap<Integer, HashMap<String, Double>> mClsMWordCount;
	private HashMap<Integer, Double> mClsCount;
	private int nRecords;

	public Learn() {
		super();
	}

	public Learn(HashMap<Integer, HashMap<String, Double>> mClsMUidCount,
			HashMap<Integer, HashMap<String, Double>> mClsMWordCount,
			HashMap<Integer, Double> mClsCount, int nRecords) {
		super();
		this.mClsMUidCount = mClsMUidCount;
		this.mClsMWordCount = mClsMWordCount;
		this.mClsCount = mClsCount;
		this.nRecords = nRecords;
	}

	public int pred(String uid, String[] wordArray) {
		double target = -Double.MAX_VALUE;
		int targetCls = 0;
		for (Integer cls : mClsCount.keySet()) {
			double t = cweight(cls, uid, wordArray);
			if (t > target) {
				target = t;
				targetCls = cls;
			}
		}
		return targetCls;
	}

	private double cweight(Integer cls, String uid, String[] wordList) {
		double weight = 0.0;
		Double  clsCount = get(mClsCount, cls);
		double classWeight = Math.log((double)clsCount / (double)nRecords);
		weight += classWeight;
		Double uidCount = get(mClsMUidCount, cls, uid);
		double uidWeight = countWeight(uidCount,clsCount);;
		weight += uidWeight;
		double wordWeight = 0.0;
		for (String word : wordList) {
			Double wordCount = get(mClsMWordCount, cls, word);
			wordWeight += countWeight(wordCount, clsCount);
		}
		wordWeight = wordWeight / wordList.length;
		weight += wordWeight;
		return weight;
	}
	private double countWeight(double member,double denominator){
		return Math.log((member + 0.000001) / (denominator+0.1));
	}

	private Double get(HashMap<Integer, Double> m, Integer key) {
		return m.getOrDefault(key, 0.0);
	}

	private Double get(HashMap<Integer, HashMap<String, Double>> m, Integer key1,
			String key2) {
		HashMap<String, Double> m2 = m.get(key1);
		if (m2 != null) {
			return (Double) m2.getOrDefault(key2, 0.0);
		} else {
			return 0.0;
		}
	}

	public void setmClsMUidCount(
			HashMap<Integer, HashMap<String, Double>> mClsMUidCount) {
		this.mClsMUidCount = mClsMUidCount;
	}

	public void setmClsMWordCount(
			HashMap<Integer, HashMap<String, Double>> mClsMWordCount) {
		this.mClsMWordCount = mClsMWordCount;
	}

	public void setmClsCount(HashMap<Integer, Double> mClsCount) {
		this.mClsCount = mClsCount;
	}

	public void setnRecords(int nRecords) {
		this.nRecords = nRecords;
	}
}
