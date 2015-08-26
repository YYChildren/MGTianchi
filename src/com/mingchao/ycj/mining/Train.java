package com.mingchao.ycj.mining;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.mingchao.ycj.util.DB;

public class Train {
	private DB db = DB.getInstance();

	public static void main(String[] args) {
		Train train = new Train();
		train.start();
	}

	public void start() {
		filter0("forward");
		filter01("forward");
		statsUid("forward");
		statsClass("forward");
		statsWord("forward");
		
		filter0("comment");
		filter01("comment");
		statsUid("comment");
		statsClass("comment");
		statsWord("comment");
		
		filter0("like");
		filter01("like");
		statsUid("like");
		statsClass("like");
		statsWord("like");
	}

	private void filter0(String type) {
		String sql = "INSERT OVERWRITE TABLE db_pred_ycj.t_weibo_train_"+type+"_class_0 "
				+ "SELECT a.uid FROM  "
				+ "(SELECT DISTINCT uid FROM db_pred.t_weibo_train_stn_class WHERE "+type+"_class=0 ) a  "
				+ "JOIN  "
				+ "(SELECT uid FROM db_pred.t_weibo_train_stn_class GROUP BY uid HAVING count(distinct "+type+"_class)=1) b  "
				+ "on a.uid = b.uid ";
		execute(sql);
	}

	private void filter01(String type) {
		String sql = "INSERT OVERWRITE TABLE db_pred_ycj.t_weibo_train_"+type+"_class_01 "
				+ " SELECT DISTINCT uid FROM db_pred.t_weibo_train_stn_class WHERE "+type+"_class <=1 "
				+ " AND uid NOT IN (SELECT uid FROM db_pred_ycj.t_weibo_train_"+type+"_class_0) "
				+ " AND uid NOT IN (SELECT DISTINCT uid FROM db_pred.t_weibo_train_stn_class WHERE "+type+"_class > 1)";
		execute(sql);
	}

	private void statsUid(String type) {
		String sql = "INSERT OVERWRITE TABLE db_pred_ycj.t_weibo_train_"+type+"_class_uid_count "
				+ " SELECT d."+type+"_class,d.uid,CAST(COUNT(d.uid) AS INT) count FROM "
				+ "  (SELECT a.uid,a."+type+"_class FROM db_pred.t_weibo_train_stn_class a "
				+ " LEFT JOIN db_pred_ycj.t_weibo_train_"+type+"_class_0 b ON a.uid = b.uid "
				+ " LEFT JOIN db_pred_ycj.t_weibo_train_"+type+"_class_01 c ON a.uid = c.uid "
				+ " WHERE b.uid IS NULL AND c.uid IS NULL) d "
				+ " GROUP BY d."+type+"_class,d.uid";
		execute(sql);
	}
	
	private void statsClass(String type) {
		String sql = "INSERT OVERWRITE TABLE db_pred_ycj.t_weibo_train_"+type+"_class_count "
				+ "SELECT d."+type+"_class,CAST(COUNT(d."+type+"_class) AS INT) count FROM "
				+ "(SELECT a."+type+"_class FROM db_pred.t_weibo_train_stn_class a LEFT JOIN db_pred_ycj.t_weibo_train_"+type+"_class_0 b "
				+ "ON a.uid = b.uid LEFT JOIN db_pred_ycj.t_weibo_train_"+type+"_class_01 c "
				+ "ON a.uid = c.uid WHERE b.uid IS NULL AND c.uid IS NULL) d GROUP BY d."+type+"_class";
		execute(sql);
	}

	private void statsWord(String type) {
		String sql = "SELECT a.segs,a."+type+"_class FROM db_pred.t_weibo_train_stn_class a "
				+ " LEFT JOIN db_pred_ycj.t_weibo_train_"+type+"_class_0 b ON a.uid = b.uid "
				+ " LEFT JOIN db_pred_ycj.t_weibo_train_"+type+"_class_01 c ON a.uid = c.uid "
				+ " WHERE b.uid IS NULL AND c.uid IS NULL";
		System.out.println(sql);
		Connection conn = null;
		Statement stmt = null;
		try {
			System.out.println("----begin----");
			conn = db.getConnection();
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			HashMap<Integer, HashMap<String, Integer>> classWordCount = new HashMap<>();
			//记录类型对应的词的统计
			while (rs.next()) {
				String segsStr = rs.getString(1);
				int destClass = rs.getInt(2);
				HashMap<String, Integer> wordCount = classWordCount
						.getOrDefault(destClass, new HashMap<>());
				int length = segsStr.length();
				segsStr = segsStr.substring(1, length - 1);
				String[] segs = segsStr.split(", ");
				int segLen = segs.length;
				for (int i = 0; i < segLen; ++i) {
					String word;
					if (segs[i].equals("[")
							&& (i + 2 < segLen && segs[i + 2].equals("]"))) {
						word = segs[i] + segs[i + 1] + segs[i + 2];
						i +=2;
					} else {
						word = segs[i];
					}
					int count = wordCount.getOrDefault(word, 0);
					wordCount.put(word, count + 1);
				}
				classWordCount.put(destClass, wordCount);
			}
			
			//清空表
			stmt.execute("INSERT OVERWRITE db_pred_ycj.t_weibo_train_"+type+"_class_word_count "
					+ "SELECT * FROM db_pred_ycj.t_weibo_train_"+type+"_class_word_count LIMIT 0");
			System.out.println("----start write----");
			String filename = "E:/WeiboPred/train/t_weibo_train_"+type+"_class_word_count.tsv";
			PrintWriter pw = null;
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			if (!classWordCount.isEmpty()) {
				for (Map.Entry<Integer, HashMap<String, Integer>> cwc : classWordCount
						.entrySet()) {
					int destClass = cwc.getKey();
					for (Map.Entry<String, Integer> wc : cwc.getValue()
							.entrySet()) {
						String word = wc.getKey();
						int count = wc.getValue();
						pw.print(destClass+ "\t" + word +"\t" + count + "\n");
					}
				}
			}
			pw.flush();
			pw.close();	
			System.out.println("----write finish----");
		} catch (InterruptedException | SQLException | IOException e) {
			e.printStackTrace();
		} finally {
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
	}

	void execute(String sql) {
		System.out.println(sql);
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = db.getConnection();
			stmt = conn.createStatement();
			boolean r = stmt.execute(sql);
			System.out.println(r);
		} catch (InterruptedException | SQLException e) {
			e.printStackTrace();
		} finally {
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
	}
}
