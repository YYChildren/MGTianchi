import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mingchao.ycj.util.DB;
 
public class ClouderaImpalaJdbcExample {
	private static String SQL_STATEMENT = "SELECT * FROM t_weibo_pred LIMIT 1";
//	private static String SQL_STATEMENT = "SHOW TABLES";
	
	public static void main(String[] args) {
 
		DB db = DB.getInstance();
		Connection conn = null;
		try {
			conn = db.getConnection();
			Statement stmt = conn.createStatement();
 
			ResultSet rs = stmt.executeQuery(SQL_STATEMENT);
 
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