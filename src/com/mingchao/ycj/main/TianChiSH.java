package com.mingchao.ycj.main;

import java.io.BufferedReader;
import java.io.PrintWriter;

import com.mingchao.ycj.util.RawIO;
import com.mingchao.ycj.util.SHFactory;
import com.mingchao.ycj.util.SentenceHandler;

public class TianChiSH {

	private static void startSh(BufferedReader br, PrintWriter pw, SHFactory shf)
			throws Exception {
		String line;
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.equals("")) {
				continue;
			}
			String[] a = line.split("\t");
			String content = a[a.length - 1];
			SentenceHandler sh = shf.buildHandler();
			sh.analyze(content);
			pw.print(line);
			pw.print('\t');
			pw.print(sh.getSegs());
			pw.print('\t');
			pw.print(sh.getTags());
			pw.print('\t');
			pw.print(sh.getNers());
			pw.print('\n');
		}
	}

	public static void main(String[] args) {
		String transFileIn = "E:/WeiboPred/in/weibo_train_data.tsv";
		String predFileIn = "E:/WeiboPred/in/weibo_predict_data.tsv";
		String transFileOut = "E:/WeiboPred/in/weibo_train_out.tsv";
		String predFileOut = "E:/WeiboPred/in/weibo_predict_out.tsv";
		String cwsModel = "E:/Children/data/3.2.0/ltp_data/cws.model";
		String posModel = "E:/Children/data/3.2.0/ltp_data/pos.model";
		String nerModel = "E:/Children/data/3.2.0/ltp_data/ner.model";
		SHFactory shf = SHFactory.getInstance(cwsModel, posModel, nerModel);
		try {
			BufferedReader transBR = RawIO.openReader(transFileIn);
			BufferedReader predBR = RawIO.openReader(predFileIn);
			PrintWriter transPW = RawIO.openWriter(transFileOut);
			PrintWriter predPW = RawIO.openWriter(predFileOut);
			startSh(transBR, transPW, shf);
			RawIO.close(transBR);
			RawIO.close(transPW);
			startSh(predBR, predPW, shf);
			RawIO.close(predBR);
			RawIO.close(predPW);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
