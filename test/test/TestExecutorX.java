package test;

import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.mingchao.ycj.util.ExecutorX;

public class TestExecutorX {

	@Test
	public void test() {
		CountDownLatch lcd = new CountDownLatch(3); 
		ExecutorX.getInstance().execute(new Runnable(){
			@Override
			public void run() {
				System.out.println(1);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					lcd.countDown();
				}
			}
			
		});
		ExecutorX.getInstance().execute(new Runnable(){
			@Override
			public void run() {
				System.out.println(2);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					lcd.countDown();
				}
			}
			
		});
		ExecutorX.getInstance().execute(new Runnable(){
			@Override
			public void run() {
				System.out.println(3);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					lcd.countDown();
				}
			}
		});
		try {
			lcd.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.out.println(ExecutorX.getInstance().getActiveCount());
		System.out.println(4);
//		fail("Not yet implemented");
	}

}
