package test;

public class LibTest {
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary("segmentor");
	}
}
