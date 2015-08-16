package com.mingchao.ycj.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.mingchao.ycj.util.SHFactory;
import com.mingchao.ycj.util.SentenceHandler;

public class TianChiSH {

	private static void startSh(BufferedReader br,PrintWriter pw,SHFactory shf) throws Exception{
		String line;
		while( (line= br.readLine()) != null  ){
			line = line.trim();
			if(line.equals("")){
				continue;
			}
			String[] a = line.split("\t");
			String content = a[a.length - 1];
			SentenceHandler sh = shf.buildHandler();
			sh.analyze(content);
			pw.print(content);
			pw.print('\t');
			pw.print(sh.getSegs());
			pw.print('\t');
			pw.print(sh.getTags());
			pw.print('\t');
			pw.print(sh.getNers());
			pw.println();
			
		}
		
	}
	public static void main(String[] args) {
		String transFileIn = "G:/tianchi/weibo_train_data.tsv";
		String predFileIn = "G:/tianchi/weibo_predict_data.tsv";
		String transFileOut = "G:/tianchi/weibo_train_data_out.tsv";
		String predFileOut = "G:/tianchi/weibo_predict_out.tsv";
		String cwsModel = "E:/Children/data/3.2.0/ltp_data/cws.model";
		String posModel = "E:/Children/data/3.2.0/ltp_data/pos.model";
		String nerModel = "E:/Children/data/3.2.0/ltp_data/ner.model";
		SHFactory shf = SHFactory.getInstance(cwsModel, posModel, nerModel);
		try {
			BufferedReader transBR = new BufferedReader(new FileReader(transFileIn));
			BufferedReader predBR = new BufferedReader(new FileReader(predFileIn));
			PrintWriter transPW = new PrintWriter(new BufferedWriter(new FileWriter(transFileOut)));
			PrintWriter predPW = new PrintWriter(new BufferedWriter(new FileWriter(predFileOut)));
			startSh(transBR,transPW,shf);
			transBR.close();
			transPW.close();
			startSh(predBR,predPW,shf);
			predBR.close();
			predPW.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
