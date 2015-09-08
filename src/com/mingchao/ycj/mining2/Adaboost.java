package com.mingchao.ycj.mining2;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;

import com.google.common.io.Files;
import com.mingchao.ycj.mining2.model.TargetClass;
import com.mingchao.ycj.util.RawIO;

public class Adaboost {
	public static void main(String[] args) {
		Adaboost ada = new Adaboost();
		ada.start(Init.getBasePath());
	}

	private String dataSrcPath = null;
	private int N;

	private Double a;
	private ArrayList<Double> D = null;
	// forward相关信息
	private HashMap<Integer, Double> mFClsCount = null;
	private HashMap<Integer, HashMap<String, Double>> mFClsMUidCount = null;
	private HashMap<Integer, HashMap<String, Double>> mFClsMWordCount = null;
	// comment 相关信息
	private HashMap<Integer, Double> mCClsCount = null;
	private HashMap<Integer, HashMap<String, Double>> mCClsMUidCount = null;
	private HashMap<Integer, HashMap<String, Double>> mCClsMWordCount = null;
	// like 相关信息
	private HashMap<Integer, Double> mLClsCount = null;
	private HashMap<Integer, HashMap<String, Double>> mLClsMUidCount = null;
	private HashMap<Integer, HashMap<String, Double>> mLClsMWordCount = null;

	public final int[] forwardSplClass = { 0, 1, 22, 208, 1102, 2292, 73450 };
	public final int[] commentSplClass = { 0, 1, 9, 37, 305, 1326, 29516 };
	public final int[] likeSplClass = { 0, 1, 9, 81, 1326, 2294, 6861 };

	public void start(String basePath) {
		try {
			String inPath = basePath + "/train_class.tsv";
			String inNPath = basePath + "/train_class_N.tsv";
			String modelPath;
			String paraPath;
			System.out.println("Adaboost initting");
			init(inPath, inNPath);
			System.out.println("Adaboost initted");
			for (int i = 0; i < 10; i++) {
				modelPath = basePath + "/bayes_" + i + ".mod";
				System.out.println("Updating model");
				updateModel(modelPath);
				System.out.println("Updated model");
				System.out.println("GenAD ing");
				genAD();
				System.out.println("GenAD ed");
				paraPath  = basePath + "/bayes_" + i + ".para";
				Files.write(a.toString().getBytes(RawIO.FILE_ENCODE), new File(paraPath));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void init(String inPath, String inNPath) throws IOException {
		dataSrcPath = inPath;
		N = Integer.parseInt(Files.readFirstLine(new File(inNPath),
				Charset.forName(RawIO.FILE_ENCODE)));
		D = new ArrayList<Double>(N);
		Double w = 1.0 / N;
		for (int i = 0; i < N; i++) {
			D.add(w);
		}
	}

	public void updateModel(String modelPath) throws IOException {
		// forward相关信息
		mFClsCount = new HashMap<>();
		mFClsMUidCount = new HashMap<>();
		mFClsMWordCount = new HashMap<>();

		// comment 相关信息
		mCClsCount = new HashMap<>();
		mCClsMUidCount = new HashMap<>();
		mCClsMWordCount = new HashMap<>();

		// like 相关信息
		mLClsCount = new HashMap<>();
		mLClsMUidCount = new HashMap<>();
		mLClsMWordCount = new HashMap<>();

		Iterator<Double> dit = D.iterator();
		String line = null;
		BufferedReader br = RawIO.openReader(dataSrcPath);
		while ((line = br.readLine()) != null) {
			Double w = dit.next();
			String[] f = line.split("\t");
			String uid = f[0];
			String[] words = f[2].split(", ");
			Integer forwardClass = Integer.parseInt(f[6]);
			Integer commentClass = Integer.parseInt(f[7]);
			Integer likeClass = Integer.parseInt(f[8]);

			Double newWeight = w * N;

			mapAdd(mFClsCount, forwardClass, newWeight);
			mapAdd(mCClsCount, commentClass, newWeight);
			mapAdd(mLClsCount, likeClass, newWeight);

			mapAdd(mFClsMUidCount, forwardClass, uid, newWeight);
			mapAdd(mCClsMUidCount, commentClass, uid, newWeight);
			mapAdd(mLClsMUidCount, likeClass, uid, newWeight);

			for (String word : words) {
				mapAdd(mFClsMWordCount, forwardClass, word, newWeight);
				mapAdd(mCClsMWordCount, commentClass, word, newWeight);
				mapAdd(mLClsMWordCount, likeClass, word, newWeight);
			}
		}

		RawIO.close(br);

		ModelSer.writeModel(mFClsCount, mFClsMUidCount, mFClsMWordCount,
				mCClsCount, mCClsMUidCount, mCClsMWordCount, mLClsCount,
				mLClsMUidCount, mLClsMWordCount, modelPath);

	}

	private void genAD() throws IOException {

		CountDownLatch lcd = new CountDownLatch(3);

		Classify forwardClassify = new Classify(dataSrcPath, mFClsMUidCount,
				mFClsMWordCount, mFClsCount, forwardSplClass,N, lcd);
//		ExecutorX.getInstance().execute(forwardClassify);
		forwardClassify.run();
//		ArrayList<TargetClass> forwardClassL = new ArrayList<>();
		
		ArrayList<TargetClass> forwardClassL = forwardClassify.getcList();
		System.out.println(forwardClassL.size());
		forwardClassify = null;
		System.gc();
		

		Classify commentClassify = new Classify(dataSrcPath, mCClsMUidCount,
				mCClsMWordCount, mCClsCount, commentSplClass,N, lcd);
//		ExecutorX.getInstance().execute(commentClassify);
		commentClassify.run();
//		ArrayList<TargetClass> commentClassL = new ArrayList<>();
		
		ArrayList<TargetClass> commentClassL = commentClassify.getcList();
		System.out.println(commentClassL.size());
		commentClassify = null;
		System.gc();
		

		Classify likeClassify = new Classify(dataSrcPath, mLClsMUidCount,
				mLClsMWordCount, mLClsCount, likeSplClass,N,lcd);
//		ExecutorX.getInstance().execute(likeClassify);
		likeClassify.run();
		
//		try {
//			lcd.await();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		ArrayList<TargetClass> likeClassL = likeClassify.getcList();
		System.out.println(likeClassL.size());
		lcd = null;
		likeClassify = null;
		System.gc();
		
		BufferedReader br = RawIO.openReader(dataSrcPath); 
		Iterator<TargetClass> fit = forwardClassL.iterator();
		Iterator<TargetClass> cit = commentClassL.iterator();
		Iterator<TargetClass> lit = likeClassL.iterator();

		Double m = 0.0;
		Double f = 0.0;
		ArrayList<Integer> wRate = new ArrayList<Integer>(N);
		int i = 0;
		String line = null;
		while((line = br.readLine()) != null){
			String fields[] = line.split("\t");
			Integer sourceForward =Integer.parseInt( fields[3]);
			Integer sourceComment = Integer.parseInt( fields[4]);
			Integer sourceLike = Integer.parseInt( fields[5]);
			Integer forwardClass = fit.next().getTargetClass();
			Integer commentClass = cit.next().getTargetClass();
			Integer likeClass = lit.next().getTargetClass();
			Integer counti1 = sourceForward + sourceComment + sourceLike;
			if (counti1 > 100) {
				counti1 = 100 + 1;
			} else {
				++counti1;
			}
			Double def = Math.abs((double) (forwardClass - sourceForward))
					/ (sourceForward + 5);
			Double dec = Math.abs((double) (commentClass - sourceComment))
					/ (sourceComment + 3);
			Double del = Math.abs((double) (likeClass - sourceLike))
					/ (sourceLike + 3);
			Double precisioni = 1 - 0.5 * def - 0.25 * dec - 0.25 * del;
			Double sgn = sgn(precisioni - 0.8);
			m += counti1 * sgn;
			f += counti1;
			if (sgn == 0.0) {
				D.set(i, -D.get(i++));// 错误分类
			}
			wRate.add(counti1);//
		}
		RawIO.close(br);

		Double precision = m / f;
		System.out.println("Current correct rate: " + precision);

		Double e = 1 - precision;

		a = (1.0 / 2) * Math.log((1 - e) / e);
		// save

		// 更新 D
		Double a0 = Math.abs(a);
		Double z = 0.0;
		int dz = D.size();
		for (int j = 0; j < dz; j++) {
			Double wj = D.get(j);
			if (wj > 0.0) {
				wj = wj * Math.exp(-a0);// 正确分类
			} else {
				wj = -wj * Math.exp(a0 + Math.log(Math.log(wRate.get(j)) + 1));// 错误分类
			}
			z += wj;
			D.set(j, wj);
		}
		for (int j = 0; j < dz; j++) {
			D.set(j, D.get(j) / z);
		}
	}

	private Double sgn(Double s) {
		if (s > 0.0) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	private void mapAdd(HashMap<Integer, Double> map, Integer key, Double value) {
		Double oldValue = map.getOrDefault(key, 0.0);
		map.put(key, oldValue + value);
	}

	private void mapAdd(HashMap<Integer, HashMap<String, Double>> map,
			Integer key1, String key2, Double value) {
		HashMap<String, Double> map2 = map.getOrDefault(key1, new HashMap<>());
		Double oldValue = map2.getOrDefault(key2, 0.0);
		map2.put(key2, oldValue + value);
		map.put(key1, map2);
	}

	private static class Classify implements Runnable {

		private ArrayList<TargetClass> cList;
		private String dataSrcPath;
		private HashMap<Integer, HashMap<String, Double>> mClsMUidCount;
		private HashMap<Integer, HashMap<String, Double>> mClsMWordCount;
		private HashMap<Integer, Double> mClsCount;
		private int[] splClass;
		private int N;
		private CountDownLatch lcd;
		

		public Classify(String dataSrcPath,
				HashMap<Integer, HashMap<String, Double>> mClsMUidCount,
				HashMap<Integer, HashMap<String, Double>> mClsMWordCount,
				HashMap<Integer, Double> mClsCount, int[] splClass,int N,
				CountDownLatch lcd) {
			super();
			this.dataSrcPath = dataSrcPath;
			this.mClsMUidCount = mClsMUidCount;
			this.mClsMWordCount = mClsMWordCount;
			this.mClsCount = mClsCount;
			this.splClass = splClass;
			this.N = N;
			this.lcd = lcd;
		}

		@Override
		public void run() {
			try {
				NativeBayesLearn lm = new NativeBayesLearn(dataSrcPath,
						mClsMUidCount, mClsMWordCount, mClsCount,N, splClass);
				cList = lm.pred();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				lcd.countDown();
			}

		}

		public ArrayList<TargetClass> getcList() {
			return cList;
		}
	}
}
