
import java.io.File;  
import java.net.URL;

import org.junit.Test;
  
public class PathTest {  
	@Test
	public static  void main(String [] args) {
		try {
			test();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    public static void test() throws Exception {  
    	URL gdufsSearchXmlUrl = ClassLoader.getSystemResource("gdufs_search.xml");////////
    	System.out.println(gdufsSearchXmlUrl);
    	File f = new File("gdufs_search.xml");
    	System.out.println(f.getAbsolutePath());
        System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));  
  
        System.out.println(PathTest.class.getClassLoader().getResource(""));  
  
        System.out.println(ClassLoader.getSystemResource(""));  
        System.out.println(PathTest.class.getResource(""));  ////////
        
        System.out.println(PathTest.class.getResource("/"));////////
        //Class文件所在路径
        System.out.println(new File("/").getAbsolutePath());  
        System.out.println(System.getProperty("user.dir"));  /////////
        System.out.println(System.getProperty("java.library.path"));
    }  
} 