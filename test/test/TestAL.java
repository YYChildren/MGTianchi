package test;
import java.util.ArrayList;

import org.junit.Test;


public class TestAL {
	@Test
	public void testal(){
		long t1 = System.currentTimeMillis();
		ArrayList<Double> nn = new ArrayList<Double>(2000000);
		for (int i = 0; i < 2000000; i++) {
			nn.add((double)i);
		}
		long t2 = System.currentTimeMillis();
		System.out.println(t2-t1);
	}
}
