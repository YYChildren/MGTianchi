
public class TestString {
	public static void main(String[] args) {
		String word = "a\\a\\a'a'a";
		word = word.replace("\\", "\\\\");
		System.out.println(word);
		word = word.replace("\'", "\\\'");;
		System.out.println(word);
	}
}
