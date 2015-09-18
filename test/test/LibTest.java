package test;

import org.junit.Test;

import com.mingchao.ycj.util.SHFactory;
import com.mingchao.ycj.util.SentenceHandler;

public class LibTest {
	public static void main(String[] args) {
		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary("segmentor");
	}
	@Test
	public void testSTN(){
		String cwsModel = "E:/Children/data/3.2.0/ltp_data/cws.model";
		String posModel = "E:/Children/data/3.2.0/ltp_data/pos.model";
		String nerModel = "E:/Children/data/3.2.0/ltp_data/ner.model";
		SentenceHandler sh = SHFactory.getInstance(cwsModel, posModel, nerModel).buildHandler();
		try {
			sh.analyze("我是中国人");
			System.out.println(sh.getSegs());
			System.out.println(sh.getTags());
			System.out.println(sh.getNers());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
