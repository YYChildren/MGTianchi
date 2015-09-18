package com.mingchao.ycj.mining2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.google.common.io.Files;
import com.mingchao.ycj.mining2.model.TargetClass;
import com.mingchao.ycj.mining2.model.TargetFinalClass;
import com.mingchao.ycj.util.ObjectIO;
import com.mingchao.ycj.util.RawIO;

public class AdaPred {
	private Double A;
	ArrayList<TargetFinalClass> atl;
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
	

	private String nth;
	private String basePath;
	private String basePath2;
	private String NPath;
	private String predSrcPath;
	private String predNPath ;
	private int nModels;
	private int N;
	private int predN;
	

	public static void main(String[] args) {
		try {
			new AdaPred("2",10).pred();
			//new AdaPred("3").r();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public AdaPred(String nth,int nModels){
		init(nth,nModels);
	}

	private void init(String nth,int nModels) {
		this.nth = nth;
		this.nModels =  nModels;
		basePath = Init.getBasePath();
		basePath2 = basePath + "/" + this.nth;
		NPath = basePath + "/train_class_N.tsv";
		predNPath = basePath + "/pred_src_N.tsv";
		predSrcPath = basePath + "/pred_src.tsv";
		try {
			N = Integer.parseInt(Files.readFirstLine(new File(NPath),
					Charset.forName(RawIO.FILE_ENCODE)));
			predN = Integer.parseInt(Files.readFirstLine(new File(predNPath),
					Charset.forName(RawIO.FILE_ENCODE)));
		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void pred() throws FileNotFoundException, IOException,
			ClassNotFoundException {
		m();
		r();
	}
	
	@SuppressWarnings("unchecked")
	public void m() throws IOException, ClassNotFoundException{
		for (int i = 0; i < nModels; i++) {
			/*----------------------初始化每个分类模型--------------------------------------*/
			String modelPath = basePath2 + "/bayes_" + i + ".mod";
			ArrayList<Object> mapL;
			ObjectInputStream ois = ObjectIO.openInputStream(modelPath);
			mapL = (ArrayList<Object>) ois.readObject();
			/*--------------------------------------------------------------------------------*/
			mFClsCount = (HashMap<Integer, Double>) mapL.get(0);
			mFClsMUidCount = (HashMap<Integer, HashMap<String, Double>>) mapL
					.get(1);
			mFClsMWordCount = (HashMap<Integer, HashMap<String, Double>>) mapL
					.get(2);
			/*--------------------------------------------------------------------------------*/
			mCClsCount = (HashMap<Integer, Double>) mapL.get(3);
			mCClsMUidCount = (HashMap<Integer, HashMap<String, Double>>) mapL
					.get(4);
			mCClsMWordCount = (HashMap<Integer, HashMap<String, Double>>) mapL
					.get(5);
			/*--------------------------------------------------------------------------------*/
			mLClsCount = (HashMap<Integer, Double>) mapL.get(6);
			mLClsMUidCount = (HashMap<Integer, HashMap<String, Double>>) mapL
					.get(7);
			mLClsMWordCount = (HashMap<Integer, HashMap<String, Double>>) mapL
					.get(8);
			ObjectIO.close(ois);

			
			/*----------------------针对各个基础分类器分类--------------------------------------*/
			NativeBayesLearn fm = new NativeBayesLearn(predSrcPath,
					mFClsMUidCount, mFClsMWordCount, mFClsCount, N,
					Adaboost.forwardSplClass);
			ArrayList<TargetClass> forwardClassList = fm.pred();
			/*--------------------------------------------------------------------------------*/
			NativeBayesLearn cm = new NativeBayesLearn(predSrcPath,
					mCClsMUidCount, mCClsMWordCount, mCClsCount, N,
					Adaboost.commentSplClass);
			ArrayList<TargetClass> commentClassList = cm.pred();
			/*--------------------------------------------------------------------------------*/
			NativeBayesLearn lm = new NativeBayesLearn(predSrcPath,
					mLClsMUidCount, mLClsMWordCount, mLClsCount, N,
					Adaboost.likeSplClass);
			ArrayList<TargetClass> likeClassList = lm.pred();
			

			/*----------------------输出基础分类器结果--------------------------------------*/
			String outPath = basePath2 + "/bayes_" + i + ".brs";
			PrintWriter pw = RawIO.openWriter(outPath);
			for (Iterator<TargetClass> iterator = forwardClassList.iterator(), cit = commentClassList
					.iterator(), lit = likeClassList.iterator(); iterator
					.hasNext();) {
				TargetClass fc = iterator.next();
				TargetClass cc = cit.next();
				TargetClass lc = lit.next();
				String uid = fc.getUid();
				String mid = fc.getMid();
				Integer forwardClass = fc.getTargetClass();
				Integer commentClass = cc.getTargetClass();
				Integer likeClass = lc.getTargetClass();
				pw.print(uid + "\t" + mid + "\t" + forwardClass + ","
						+ commentClass + "," + likeClass + "\n");
			}
			RawIO.close(pw);
		}
	}
	
	public void r() throws NumberFormatException, IOException{
		/*----------------------初始化结果集--------------------------------------*/
		atl = new ArrayList<TargetFinalClass>(predN);
		for (int i = 0; i < predN; ++i) {
			atl.add(new TargetFinalClass());
		}

		/*----------------------分类器投票--------------------------------------*/
		for (int i = 0; i < nModels; i++) {
			String weightPath = basePath2 + "/bayes_" + i + ".para";
			A = Double.parseDouble(Files.readFirstLine(new File(weightPath),
					Charset.forName(RawIO.FILE_ENCODE)));
			String baseRsPath = basePath2+ "/bayes_" + i + ".brs";
			BufferedReader br = RawIO.openReader(baseRsPath);
			for (int j = 0; j < predN; j++) {
				String[] f = br.readLine().split("\t");
				TargetFinalClass tfc = atl.get(j);
				if (i == 0) {
					tfc.setUid(f[0]);
					tfc.setMid(f[1]);
				}
				String[] ff = f[2].split(",");
				tfc.addForward(Integer.parseInt(ff[0]), A);
				tfc.addComment(Integer.parseInt(ff[1]), A);
				tfc.addLike(Integer.parseInt(ff[2]), A);
			}
			RawIO.close(br);
		}
		/*----------------------输出分类结果集--------------------------------------*/
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String ymdd = sdf.format(d);
		String rsPath = basePath2 + "/" + ymdd + ".txt";
		PrintWriter pw = RawIO.openWriter(rsPath);
		for (int i = 0; i < predN; ++i) {
			TargetFinalClass tfc = atl.get(i);
			pw.print(tfc.getUid() + "\t" 
					+ tfc.getMid() + "\t"
					+ tfc.getForward() + ","
					+ tfc.getComent() + ","
					+ tfc.getLike() + "\n");
		}
		RawIO.close(pw);
	}
}
