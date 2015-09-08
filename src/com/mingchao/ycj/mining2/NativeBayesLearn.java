package com.mingchao.ycj.mining2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.google.common.io.Files;
import com.mingchao.ycj.mining2.model.TargetClass;
import com.mingchao.ycj.util.RawIO;

public class NativeBayesLearn {

	private String dataSrcPath;
	private HashMap<Integer, HashMap<String, Double>> mClsMUidCount;
	private HashMap<Integer, HashMap<String, Double>> mClsMWordCount;
	private HashMap<Integer, Double> mClsCount;
	private int[] splClass;

	private ArrayList<TargetClass> classifyResult;

	@SuppressWarnings("unchecked")
	public NativeBayesLearn(String dataSrcPath,
			HashMap<Integer, HashMap<String, Double>> mClsMUidCount,
			HashMap<Integer, HashMap<String, Double>> mClsMWordCount,
			HashMap<Integer, Double> mClsCount,int N, int[] splClass) {
		super();
		this.dataSrcPath = dataSrcPath;
		this.mClsMUidCount = (HashMap<Integer, HashMap<String, Double>>) mClsMUidCount
				.clone();
		this.mClsMWordCount = (HashMap<Integer, HashMap<String, Double>>) mClsMWordCount
				.clone();
		this.mClsCount = (HashMap<Integer, Double>) mClsCount.clone();
		this.splClass = splClass;
		classifyResult = new ArrayList<TargetClass>(N);
	}

	public ArrayList<TargetClass> pred() {
		pred2();
		return classifyResult;
	}

	private void pred2() {
		classify();
		Collections.sort(classifyResult);
	}

	private void classify() {
		File src = new File(dataSrcPath);
		File tmpf1 = new File(Init.getBasePath() + "/" + UUID.randomUUID());
		File tmpf2 = new File(Init.getBasePath() + "/" + UUID.randomUUID());
		try {
			Files.copy(src, tmpf1);
			File in = tmpf1;
			File out = tmpf2;
			for (int maxClass : splClass) {
				if (!learn(maxClass, in, out)) {
					break;
				}
				Iterator<Map.Entry<Integer, Double>> it = mClsCount.entrySet()
						.iterator();
				while (it.hasNext()) {
					Map.Entry<Integer, Double> e = it.next();
					Integer key = e.getKey();
					if (key <= maxClass) {
						mClsMUidCount.remove(key);
						mClsMWordCount.remove(key);
						it.remove();
					}
				}
				File t = in;
				in = out;
				out = t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tmpf1.delete();
			tmpf2.delete();
		}
	}

	private boolean learn(int maxClass, File in, File out) throws IOException {

		int lmaxClass = Integer.MIN_VALUE;

		HashMap<Integer, HashMap<String, Double>> lmClsMUidCount = new HashMap<>();
		HashMap<Integer, HashMap<String, Double>> lmClsMWordCount = new HashMap<>();
		HashMap<Integer, Double> lmClsCount = new HashMap<>();

		// 合并小类uid
		for (HashMap.Entry<Integer, HashMap<String, Double>> clsUidCount : mClsMUidCount
				.entrySet()) {
			Integer cls = clsUidCount.getKey();
			if (cls > maxClass) {
				HashMap<String, Double> newMUidCount = clsUidCount.getValue();
				HashMap<String, Double> mUidCount = lmClsMUidCount
						.getOrDefault(lmaxClass, new HashMap<>());
				for (Map.Entry<String, Double> uidCount : mUidCount.entrySet()) {
					String uid = uidCount.getKey();
					Double count = uidCount.getValue();
					if (newMUidCount.containsKey(uid)) {
						mUidCount.put(uid, count + newMUidCount.get(uid));
					}
				}
				for (Map.Entry<String, Double> uidCount : newMUidCount
						.entrySet()) {
					String uid = uidCount.getKey();
					Double count = uidCount.getValue();
					if (!mUidCount.containsKey(uid)) {
						mUidCount.put(uid, count);
					}
				}
				lmClsMUidCount.put(lmaxClass, mUidCount);
			} else {
				lmClsMUidCount.put(cls, clsUidCount.getValue());
			}
		}

		// 合并小类word
		for (HashMap.Entry<Integer, HashMap<String, Double>> clsWordCount : mClsMWordCount
				.entrySet()) {
			Integer cls = clsWordCount.getKey();
			if (cls > maxClass) {
				HashMap<String, Double> newMWordCount = clsWordCount.getValue();
				HashMap<String, Double> mWordCount = lmClsMWordCount
						.getOrDefault(lmaxClass, new HashMap<>());
				for (Map.Entry<String, Double> wordCount : mWordCount
						.entrySet()) {
					String word = wordCount.getKey();
					Double count = wordCount.getValue();
					if (newMWordCount.containsKey(word)) {
						mWordCount.put(word, count + newMWordCount.get(word));
					}
				}
				for (Map.Entry<String, Double> wordCount : newMWordCount
						.entrySet()) {
					String word = wordCount.getKey();
					Double count = wordCount.getValue();
					if (!mWordCount.containsKey(word)) {
						mWordCount.put(word, count);
					}
				}
				lmClsMWordCount.put(lmaxClass, mWordCount);
			} else {
				lmClsMWordCount.put(cls, clsWordCount.getValue());
			}

		}

		// 合并小类计数
		for (HashMap.Entry<Integer, Double> clsCount : mClsCount.entrySet()) {
			Integer cls = clsCount.getKey();
			if (cls > maxClass) {
				Double oldCount = lmClsCount.getOrDefault(lmaxClass, 0.0);
				lmClsCount.put(lmaxClass, clsCount.getValue() + oldCount);
			} else {
				lmClsCount.put(cls, clsCount.getValue());
			}
		}

		NativeBayes model = new NativeBayes(lmClsMUidCount, lmClsMWordCount,
				lmClsCount);
		String line = null;
		BufferedReader br = RawIO.openReader(in);
		PrintWriter pw = RawIO.openWriter(out);
		boolean hasNext = false;
		while ((line = br.readLine()) != null) {
			String[] f = line.split("\t");
			String uid = f[0];
			String mid = f[1];
			String[] wordArray = f[2].split(", ");
			int targetClass = model.pred(uid, wordArray);
			if (targetClass != lmaxClass) {
				TargetClass t = new TargetClass();
				t.setUid(uid);
				t.setMid(mid);
				t.setTargetClass(targetClass);
				classifyResult.add(t);
			} else {
				hasNext = true;
				pw.print(line + "\n");
			}
		}
		RawIO.close(pw);
		RawIO.close(br);
		return hasNext;
	}
}
