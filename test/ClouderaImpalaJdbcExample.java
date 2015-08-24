import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mingchao.ycj.util.DB;
 
public class ClouderaImpalaJdbcExample {
	private static DB db = null;
	Connection conn = null;
	@BeforeClass
	public static  void beforeClass(){
		db = DB.getInstance();
	}
	@Before
	public void beforeTest(){
		try {
			conn = db.getConnection();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void update(){
			Statement stmt;
			try {
				stmt = conn.createStatement();
				String sql = "INSERT OVERWRITE TABLE db_pred_ycj.O2 SELECT * FROM db_pred_ycj.O1 limit 0";
				int r = stmt.executeUpdate(sql);
				System.out.println(r);
				int n = stmt.executeUpdate("INSERT OVERWRITE TABLE db_pred_ycj.O2 VALUES('12111'),('2222')");
				System.out.println(n);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	@Test
	public void testLoad(){
		String filename = "/data/yangchaojun/WeiboPred/t_weibo_train_forward_class_word_count.tsv";
		String loadSql = "load data local inpath \""+ filename
				+"\" overwrite into table db_pred_ycj.t_weibo_train_forward_class_word_count";
		Statement stmt;
		try {
			stmt = conn.createStatement();
			boolean n = stmt.execute(loadSql);
			System.out.println(n);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    @After
    public void afterTest(){
    	try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
	@AfterClass
	public  static  void afterClass(){
		db.closePool();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
 
		DB db = DB.getInstance();
		Connection conn = null;
		try {
			conn = db.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM default.test");
			System.out.println("start3");
			System.out.println("\n== Begin Query Results ======================");
 
			// print the results to the console
			while (rs.next()) {
				// the example query returns one String column
				System.out.println(rs.getString(1));
			}
 
			System.out.println("== End Query Results =======================\n\n");
 
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				db.release(conn);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}