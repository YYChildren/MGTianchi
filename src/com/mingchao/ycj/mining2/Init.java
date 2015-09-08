package com.mingchao.ycj.mining2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.google.common.io.Files;
import com.mingchao.ycj.util.DB;
import com.mingchao.ycj.util.RawIO;

public class Init {
	private static String  basePath = null;
	static{
		try {
			String p = Init.class.getResource("/").toString().substring(6) + "/bashpath.properties";
			basePath = Files.readFirstLine(new File(p), Charset.forName(RawIO.FILE_ENCODE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		initTrainSrc("db_pred.t_weibo_train_stn_class", basePath+ "/train_class.tsv");
		initPredSrc("db_pred.t_weibo_pred_stn", basePath + "/pred_src.tsv");
	}
	
	public static String getBasePath(){
		return basePath;
	}

	public static void initTrainSrc(String table, String predSrcPath) {
		System.out.println("Class initing");
		Set<String> wordSet = new HashSet<String>();
		DB db = DB.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PrintWriter pw = null;
		try {
			String sql = "SELECT uid,mid,segs,forward_count,comment_count,like_count,forward_class,comment_class,like_class FROM "
					+ table + " order by uid,mid";
			conn = db.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			pw = RawIO.openWriter(predSrcPath);
			while (rs.next()) {
				String uid = rs.getString(1);
				String mid = rs.getString(2);
				String segsStr = rs.getString(3);
				Integer forwardCount = rs.getInt(4);
				Integer commentCount = rs.getInt(5);
				Integer likeCount = rs.getInt(6);
				Integer forwardClass = rs.getInt(7);
				Integer commentClass = rs.getInt(8);
				Integer likeClass = rs.getInt(9);
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
						+ "\t" + forwardCount + "\t" + commentCount + "\t"
						+ likeCount + "\t" + forwardClass + "\t" + commentClass
						+ "\t" + likeClass + "\t" + "\n";
				pw.print(line);
			}
		} catch (SQLException | InterruptedException | FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			RawIO.close(pw);
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
	
	public static void initTrainCount(String table, String srcPath){
		DB db = DB.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PrintWriter pw = null;
		try {
			String sql = "SELECT count(*) FROM "+ table;
			conn = db.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			pw = RawIO.openWriter(srcPath);
			rs.next();
			Integer count = rs.getInt(1);
			pw.print(count +"\n");
		}catch(IOException | InterruptedException | SQLException e){
			e.printStackTrace();
		}finally {
			RawIO.close(pw);
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
	}

	public static void initPredSrc(String table, String predSrcPath) {
		System.out.println("Class initing");
		Set<String> wordSet = new HashSet<String>();
		DB db = DB.getInstance();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		PrintWriter pw = null;
		try {
			String sql = "SELECT uid,mid,segs FROM " + table
					+ " order by uid,mid";
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
			RawIO.close(pw);
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

}
