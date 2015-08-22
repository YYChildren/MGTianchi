package com.mingchao.ycj.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class DB {
    private LinkedBlockingQueue<Connection> pool;
 	private static final String IMPALAD_HOST = "192.168.8.208";
 	private static final String IMPALAD_JDBC_PORT = "21050";
 	private static final String CONNECTION_URL = "jdbc:hive2://" 
 			+ IMPALAD_HOST 
 			+ ':' 
 			+ IMPALAD_JDBC_PORT 
 			+ "/"
 			+ ";auth=noSasl";
 	private static final String JDBC_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
    private int poolSize = 5;
    private static DB instance = null;
    
    //私有构造方法，禁止外部创建本类的对象，要想获得本类的对象，通过<code>getInstance</code>方法
    private DB(){
        System.out.println("创建连接池...");
        init();
    }
    
    //连接池初始化方法，读取属性文件的内容，建立连接池中的初始连接
    private void init(){
        //readConfig();
    	pool = new LinkedBlockingQueue<Connection>(poolSize);
        addConnection();
    }
    
    //返回连接池中的一个数据库连接
    public Connection getConnection() throws InterruptedException{
    		return pool.take();
    }
    
    //返回连接到连接池中
    public void release(Connection conn) throws InterruptedException{
        pool.put(conn);
    }
    
    //返回当前连接池的一个对象
    public static DB getInstance(){
        if (instance == null) {
        	synInit();
        }
        return instance;
    }
    
    //关闭连接池中的所有数据库连接
    public synchronized void closePool(){
    	Iterator<Connection> it = pool.iterator();
    	while(it.hasNext()){
    		Connection conn = it.next();
    		try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
    		it.remove();
    	}
    	pool = null;
    }

    private static synchronized void synInit(){
    	if (instance == null) {
            instance = new DB();
        }
    }
    
    //在连接池中创建初始设置的数据库连接
    private void addConnection(){
        Connection conn = null;
        for (int i = 0; i < poolSize; i++) {
            try {
                Class.forName(JDBC_DRIVER_NAME);
                conn = java.sql.DriverManager.getConnection(CONNECTION_URL);
                pool.add(conn);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
//    //读取设置连接池的属性文件
//    private void readConfig(){
//        try {
//            String path = System.getProperty("user.dir") + "\\dbpool.properties";
//            FileInputStream is = new FileInputStream(path);
//            Properties props = new Properties();
//            props.load(is);
//            this.JDBC_DRIVER_NAME = props.getProperty("JDBC_DRIVER_NAME");
//            this.username = props.getProperty("username");
//            this.password = props.getProperty("password");
//            this.url = props.getProperty("url");
//            this.poolSize = Integer.parseInt(props.getProperty("poolSize"));
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.err.println("属性文件找不到");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("读取属性文件出错");
//        }
//    }
//    
}