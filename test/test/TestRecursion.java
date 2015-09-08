package test;

public class TestRecursion {
	public static void main(String[] args) {
		Number n = new Number();
		n.setN(0);
		try {
			recursion(n);
		} catch (Exception e) {
		}
	}
	private static int recursion(Number n){
		n.setN(n.getN() + 1);
		System.out.println(n.getN());
		return recursion(n);
	}
	public static class Number{
		private long n;
		public long getN() {
			return n;
		}
		public void setN(long n) {
			this.n = n;
		}
	}
}
