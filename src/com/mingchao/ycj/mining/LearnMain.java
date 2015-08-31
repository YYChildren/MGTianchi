package com.mingchao.ycj.mining;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mingchao.ycj.util.DB;
import com.mingchao.ycj.util.FileCombine;
import com.mingchao.ycj.util.RawIO;

public class LearnMain {

	public static void main(String[] args) {
		//LearnMain.initPredSrc("db_pred.t_weibo_pred_stn");
		LearnMain lm = new LearnMain();
		int[] splClass = {0,1,22,208,1102,2292,73450};
		List<String> fPathList = lm.pred("forward", splClass);
		int[] splClass2 = {0,1,9,37,305,1326,29516};
		List<String> cPathList = lm.pred("comment", splClass2);
		int[] splClass3 = {0,1,9,81,1326,2294,6861};
		List<String> lPathList = lm.pred("like", splClass3);
		lm = null;
		System.gc();
		try {
			String foutput = LearnMain.basePath+"/forward.out";
			FileCombine.combine(fPathList, foutput);
			System.gc();
			String coutput = LearnMain.basePath+"/comment.out";
			FileCombine.combine(cPathList, coutput);
			System.gc();
			String loutput = LearnMain.basePath+"/like.out";
			FileCombine.combine(lPathList, loutput);
			System.gc();
			Date date = new Date(); 
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss"); 
			String allOut = LearnMain.basePath+"/" + df.format(date)+ ".txt";
			FileCombine.combine2(foutput, coutput, loutput, allOut);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final static String basePath = "E:/WeiboPred/learn";
	private final static String predSrcPath = basePath + "/pred_src.tsv";
	private Set<String> zeroUid;
	private Set<String> zero1Uid;
	private HashMap<Integer, HashMap<String, Integer>> mClsMUidCount;
	private HashMap<Integer, HashMap<String, Integer>> mClsMWordCount;
	private HashMap<Integer, Integer> mClsCount;
	private int nRecords = 0;
	private List<String> outputPathList;
	
	private LearnMain() {
	}

	public static void initPredSrc(String table) {
		System.out.println("Class initing");
		Set<String> wordSet = new HashSet<String>();
		DB db = DB.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PrintWriter pw = null;
		try {
			String sql = "SELECT uid,mid,segs FROM " + table;
			conn = db.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			pw = RawIO.openWriter(predSrcPath);
			while (rs.next()) {
				String uid = rs.getString("uid");
				String mid = rs.getString("mid");
				String segsStr = rs.getString("segs");
				wordSet.clear();
				int length = segsStr.length();
				segsStr = segsStr.substring(1, length - 1);
				String[] segs = segsStr.split(", ");
				int segLen = segs.length;
				for (int i = 0; i < segLen; ++i) {
					String word;
					if (segs[i].equals("[")
							&& (i + 2 < segLen && segs[i + 2].equals("]"))) {
						word = segs[i] + segs[i + 1] + segs[i + 2];
						i += 2;
					} else {
						word = segs[i];
					}
					wordSet.add(word);
				}
				String[] wordList = wordSet.toArray(new String[0]);
				String line = uid + "\t" + mid + "\t" + array2String(wordList)
						+ "\n";
				pw.print(line);
			}
		} catch (SQLException | InterruptedException | FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			RawIO.closeWriter(pw);
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				stmt.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			try {
				db.release(conn);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Class inited");
	}

	private static String array2String(String[] wordArray) {

		int len = wordArray.length;
		if (len != 0) {
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < len; ++i) {
				s.append(wordArray[i]);
				s.append(", ");
			}
			int slen = s.length();
			return s.substring(0, slen - 2);
		} else {
			return "";
		}
	}

	private void init2(String type) {
		System.out.println("Object initing");
		zeroUid = new HashSet<String>();
		zero1Uid = new HashSet<String>();
		mClsMUidCount = new HashMap<>();
		mClsMWordCount = new HashMap<>();
		mClsCount = new HashMap<>();
		outputPathList = new ArrayList<String>();
		
		DB db = DB.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			String sql = "SELECT uid FROM db_pred_ycj.t_weibo_train_" + type
					+ "_class_0";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				zeroUid.add(rs.getString(1));
			}

			sql = "SELECT uid FROM db_pred_ycj.t_weibo_train_" + type
					+ "_class_01";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				zero1Uid.add(rs.getString(1));
			}

			sql = "SELECT "+type+"_class,uid,count FROM db_pred_ycj.t_weibo_train_" + type
					+ "_class_uid_count";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int targetClass = rs.getInt(1);
				String uid = rs.getString(2);
				int count = rs.getInt(3);
				HashMap<String, Integer> m = mClsMUidCount.getOrDefault(
						targetClass, new HashMap<String, Integer>());
				m.put(uid, count);
				mClsMUidCount.put(targetClass, m);
			}

			sql = "SELECT "+type+"_class,word,count FROM db_pred_ycj.t_weibo_train_" + type
					+ "_class_word_count";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int targetClass = rs.getInt(1);
				String word = rs.getString(2);
				int count = rs.getInt(3);
				HashMap<String, Integer> m = mClsMWordCount.getOrDefault(
						targetClass, new HashMap<String, Integer>());
				m.put(word, count);
				mClsMWordCount.put(targetClass, m);
			}

			sql = "SELECT "+type+"_class,count FROM db_pred_ycj.t_weibo_train_" + type
					+ "_class_count";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int targetClass = rs.getInt(1);
				int count = rs.getInt(2);
				mClsCount.put(targetClass, count);
			}

			sql = "SELECT count(*) FROM db_pred.t_weibo_train_stn_class a "
					+ " LEFT JOIN db_pred_ycj.t_weibo_train_" + type
					+ "_class_0 b on a.uid = b.uid "
					+ " LEFT JOIN db_pred_ycj.t_weibo_train_" + type
					+ "_class_01 c on a.uid = c.uid "
					+ " WHERE b.uid IS NULL AND c.uid IS NULL";
			rs = stmt.executeQuery(sql);
			rs.next();
			nRecords = rs.getInt(1);
		} catch (InterruptedException | SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				db.release(conn);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Object inited");
	}
	
	public List<String> pred(String type, int[] splClass){
		init2(type);	
		pred2(type,splClass);
		return outputPathList;
	}
	
	private void pred2(String type, int[] splClass){
		classify(type,splClass);
	}
	
	private void filter(String inputPath,String outputPath,String otherOutputPath,Set<String> filterSet,int targetClass) {
		BufferedReader br = null;
		PrintWriter pw = null;
		PrintWriter pwo = null;
		try {
			 br = RawIO.openReader(inputPath);
			 pw = RawIO.openWriter(outputPath);
			 pwo = RawIO.openWriter(otherOutputPath);
			 String line = null;
			while((line = br.readLine()) != null){
				String[] uidMidWordL = line.split("\t");
				String uid = uidMidWordL[0];
				String mid = uidMidWordL[1];
				if (!filterSet.contains(uid)) {
					pwo.print(line + "\n");
				} else {
					pw.print(uid+"\t"+ mid + "\t" + targetClass + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			RawIO.closeWriter(pwo);
			RawIO.closeWriter(pw);
			RawIO.closeReader(br);
		}
	}

	private void classify(String type, int[] splClass) {
		DB db = DB.getInstance();
		String inputPath = predSrcPath;
		String outputPath;
		String otherOutputPath;
		for (int maxClass : splClass) {
			outputPath = basePath + "/" + type + "_" + maxClass + ".tsv";
			otherOutputPath = basePath + "/" + type + "_o_" + maxClass + ".tsv";
			outputPathList.add(outputPath);
			if(maxClass == 0){
				filter(inputPath,outputPath,otherOutputPath,zeroUid,0);
				inputPath = otherOutputPath;
			}else if(maxClass == 1){
				filter(inputPath,outputPath,otherOutputPath,zero1Uid,0);
				inputPath = otherOutputPath;
			}else{
				int lnRecords = nRecords;
				boolean hasNext = learn(inputPath, outputPath, otherOutputPath, maxClass, lnRecords);
				if(!hasNext){
					break;
				}
				//更新数据，迭代分类
				
				Iterator<Map.Entry<Integer,Integer>> it = mClsCount.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<Integer,Integer> e = it.next();
					Integer key = e.getKey();
					if(key <= maxClass){
						mClsMUidCount.remove(key);
						mClsMWordCount.remove(key);
						it.remove();
					}
				}
				inputPath = otherOutputPath;
				Connection conn = null;
				Statement stmt = null;
				ResultSet rs = null;
				try {
					String sql = "SELECT count(*) FROM db_pred.t_weibo_train_stn_class WHERE "+type + "_class > " + maxClass;
					conn = db.getConnection();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					rs.next();
					lnRecords = rs.getInt(1);
				} catch (InterruptedException | SQLException e) {
					e.printStackTrace();
					break;
				} finally {
					try {
						stmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					try {
						db.release(conn);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	private boolean learn(String inputPath, String outputPath,
			String otherOutputPath, int maxClass, int lnRecords) {

		int lmaxClass = Integer.MIN_VALUE;

		HashMap<Integer, HashMap<String, Integer>> lmClsMUidCount = new HashMap<>();
		HashMap<Integer, HashMap<String, Integer>> lmClsMWordCount = new HashMap<>();
		HashMap<Integer, Integer> lmClsCount = new HashMap<>();

		// 合并小类uid
		for (HashMap.Entry<Integer, HashMap<String, Integer>> clsUidCount : mClsMUidCount
				.entrySet()) {
			Integer cls = clsUidCount.getKey();
			if(cls > maxClass){
				HashMap<String, Integer> newMUidCount = clsUidCount.getValue();
				HashMap<String, Integer> mUidCount = lmClsMUidCount.getOrDefault(lmaxClass, new HashMap<>());
				for(Map.Entry<String, Integer> uidCount : mUidCount.entrySet()){
					String uid = uidCount.getKey();
					Integer count = uidCount.getValue();
					if(newMUidCount.containsKey(uid)){
						mUidCount.put(uid, count + newMUidCount.get(uid));
					}
				}
				for(Map.Entry<String, Integer> uidCount : newMUidCount.entrySet()){
					String uid = uidCount.getKey();
					Integer count = uidCount.getValue();
					if(!mUidCount.containsKey(uid)){
						mUidCount.put(uid, count);
					}
				}
				lmClsMUidCount.put(lmaxClass, mUidCount);
			}else{
				lmClsMUidCount.put(cls, clsUidCount.getValue());
			}
		}

		// 合并小类word
		for (HashMap.Entry<Integer, HashMap<String, Integer>> clsWordCount : mClsMWordCount
				.entrySet()) {
			Integer cls = clsWordCount.getKey();
			if(cls > maxClass){
				HashMap<String, Integer> newMWordCount = clsWordCount.getValue();
				HashMap<String, Integer> mWordCount = lmClsMWordCount.getOrDefault(lmaxClass, new HashMap<>());
				for(Map.Entry<String, Integer> wordCount : mWordCount.entrySet()){
					String word = wordCount.getKey();
					Integer count = wordCount.getValue();
					if(newMWordCount.containsKey(word)){
						mWordCount.put(word, count + newMWordCount.get(word));
					}
				}
				for(Map.Entry<String, Integer> wordCount : newMWordCount.entrySet()){
					String word = wordCount.getKey();
					Integer count = wordCount.getValue();
					if(!mWordCount.containsKey(word)){
						mWordCount.put(word, count);
					}
				}
				lmClsMWordCount.put(lmaxClass, mWordCount);
			}else{
				lmClsMWordCount.put(cls, clsWordCount.getValue());
			}
			
		}

		// 合并小类计数
		for (HashMap.Entry<Integer, Integer> clsCount : mClsCount.entrySet()) {
			Integer cls = clsCount.getKey();
			if(cls > maxClass){
				Integer oldCount = lmClsCount.getOrDefault(lmaxClass, 0);
				lmClsCount.put(lmaxClass, clsCount.getValue() + oldCount);
			} else {
				lmClsCount.put(cls, clsCount.getValue());
			}
		}

		Learn model = new Learn(lmClsMUidCount, lmClsMWordCount, lmClsCount,lnRecords);
		BufferedReader br = null;
		PrintWriter pw = null;
		PrintWriter pwo = null;
		boolean hasNext = false;
		try {
			br = RawIO.openReader(inputPath);
			pw = RawIO.openWriter(outputPath);
			pwo = RawIO.openWriter(otherOutputPath);
			String line;
			while ((line = br.readLine()) != null) {
				String[] uidMidWordL = line.split("\t");
				String uid = uidMidWordL[0];
				String mid = uidMidWordL[1];
				String[] wordArray = uidMidWordL[2].split(", ");
				int targetClass = model.pred(uid, wordArray);
				if (targetClass == lmaxClass) {
					hasNext = true;
					pwo.print(line + "\n");
				} else {
					pw.print(uid+"\t"+ mid + "\t" + targetClass + "\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			RawIO.closeWriter(pwo);
			RawIO.closeWriter(pw);
			RawIO.closeReader(br);
		}
		return hasNext; 
	}

	public void combine(){
		
	}
}
