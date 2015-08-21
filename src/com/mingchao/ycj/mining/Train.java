package com.mingchao.ycj.mining;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mingchao.ycj.util.DB;

public class Train {
	private DB db  = DB.getInstance();
	private  final String DATABASE = "db_pred";
	private  String baseTable = "t_weibo_train_stn_class";
	private  String dbTable = DATABASE + "." + baseTable;
	public static void main(String[] args) {
		Train train = new Train();
		train.start();
	}
	public void start(){
		filter0();
	}
	private void filter0(){
		Connection conn = null;
		try {
			conn = db.getConnection();
			String zeroUserSQL = "SELECT a.UID from "   
					+ "(SELECT DISTINCT UID FROM "+ dbTable +" where forward_class=0 ) a"
					+ " JOIN "
					+ "(SELECT UID FROM " + dbTable + " GROUP BY UID HAVING count(distinct forward_class)=1) b"
					+ " on a.UID = b.UID";
			System.out.println(zeroUserSQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(zeroUserSQL);
//			while (rs.next()) {
//				System.out.println(rs.getString(1));
//			}
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
		
	}
}
