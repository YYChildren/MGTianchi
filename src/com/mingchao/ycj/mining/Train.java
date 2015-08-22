package com.mingchao.ycj.mining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mingchao.ycj.util.DB;

public class Train {
	private DB db  = DB.getInstance();
	private String weiboTrain = "db_pred.t_weibo_train_stn_class";
	private String weiboTrainN0 = "db_pred_ycj.t_weibo_train_stn_class_n0";
	public static void main(String[] args) {
		Train train = new Train();
		train.start();
	}
	public void start(){
		filter0();
		filter01();
	}
	private void filter0(){
		Connection conn = null;
		try {
			conn = db.getConnection();
			String zeroUserSQL = 
					"SELECT a.UID from "   
					+ "(SELECT DISTINCT UID FROM "+weiboTrain +" where forward_class=0 ) a"
					+ " JOIN "
					+ "(SELECT UID FROM " + weiboTrain + " GROUP BY UID HAVING count(distinct forward_class)=1) b"
					+ " on a.UID = b.UID";
			System.out.println(zeroUserSQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(zeroUserSQL);
			String filename = "E:/WeiboPred/train/t_weibo_train_forward_class_0.tsv";
			write(rs,filename );
			// TODO
			loadIntoHadoop();
		} catch (InterruptedException | SQLException e) {
			e.printStackTrace();
		}finally{
			try {
				db.release(conn);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void filter01(){
		String zeroUserSQL =
				 "select a.uid from (select uid from  db_pred.t_weibo_train_stn_class GROUP BY UID HAVING count(distinct forward_class)=2) a where a.uid not in (select uid from  db_pred.t_weibo_train_stn_class where forward_class > 1)";
	}
	private void write(ResultSet rs,String filename ){
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
			while(rs.next()){
				pw.print(rs.getInt(1));
				pw.print("\n");
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}finally{
			pw.close();
		}
	}
	private void loadIntoHadoop(){
		
	}
}
