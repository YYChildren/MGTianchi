import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;


public class TestString {
	public static void main(String[] args) {
		String word = "a\\a\\a'a'a";
		word = word.replace("\\", "\\\\");
		System.out.println(word);
		word = word.replace("\'", "\\\'");;
		System.out.println(word);
	}
	@Test
	public void testLn(){
		System.out.println(111111*1.0/822868);
		System.out.println(Math.log(111111*1.0/822868 + 1));
		System.out.println(Math.log1p( 111111*1.0/822868));
		System.out.println(Math.log10(111111*1.0/822868 + 1));
		System.out.println(Math.log10(111111*1.0/822868));
		
		System.out.println(Math.log((111111*1.0 + 1.0)/(822868+822868)));
		System.out.println(Math.log(111111*1.0/822868));
		
		System.out.println(111+0.0);
		
		System.out.println(Math.log(Double.MIN_VALUE));
		System.out.println(Math.log(1/Double.MAX_VALUE));
		
		System.out.println(Math.log(0.000001));
	}
	@Test
	public void testDouble(){
		System.out.println(-106.279491904863 > (-Double.MAX_VALUE  ));
		System.out.println(-106.279491904863 > -132.12341896311406);
	}
	@Test
	public void testDate(){
		Date date = new Date(); 
		DateFormat df6 = new SimpleDateFormat("yyyyMMddhhmmss");
		System.out.print(df6.format(date));
	}
}
