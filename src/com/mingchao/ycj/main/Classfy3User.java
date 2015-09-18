package com.mingchao.ycj.main;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Set;

import com.mingchao.ycj.util.RawIO;
import com.mingchao.ycj.util.Tool;

public class Classfy3User {
	public static void main(String[] args) {
		String trainUserNormalPath = "E:/WeiboPred/out/train_user/weibo_train_user_normal.tsv";
		Set<String> trainUserNormalSet =  Tool.buildSetFromFile(trainUserNormalPath);
		System.out.println(trainUserNormalSet.size());
		String trainOut = "E:/WeiboPred/out/weibo_train_out.tsv";
		String trainOutNormal = "E:/WeiboPred/out/weibo_train_out_normal.tsv";
		filterFromSet(trainUserNormalSet,trainOut,trainOutNormal);
		System.out.println("finish");
	}
	
	public static void filterFromSet(Set<String> s, String in,String out){
		BufferedReader br=null;
		PrintWriter pw = null;
		String line = null;
		try {
			 br = RawIO.openReader(in);
			 pw = RawIO.openWriter(out);
			while((line=br.readLine()) != null){
				line = line.trim();
				if(!line.equals("") && s.contains( line.split("\t")[0]) ){
					pw.print(line);
					pw.print('\n');
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			RawIO.close(br);
			RawIO.close(pw);
		}
	}


}
