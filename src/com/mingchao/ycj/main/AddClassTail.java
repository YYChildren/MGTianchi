package com.mingchao.ycj.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.mingchao.ycj.util.RawIO;

public class AddClassTail {
	public static void main(String[] args) {
		ArrayList<Integer> forwardClassList = buildListFromFile("E:/WeiboPred/out/class1.0.txt");
		ArrayList<Integer> commentClassList = buildListFromFile("E:/WeiboPred/out/class0.6.txt");
		
		@SuppressWarnings("unchecked")
		ArrayList<Integer> likeClassList = (ArrayList<Integer>) commentClassList.clone();
		String trainOut = "E:/WeiboPred/out/weibo_train_stn.tsv";
		String trainOutClass = "E:/WeiboPred/out/weibo_train_stn_class.tsv";
		addClass(forwardClassList,commentClassList,likeClassList,trainOut,trainOutClass);
		
	}

	private static void addClass(ArrayList<Integer> forwardClassList,
			ArrayList<Integer> commentClassList,
			ArrayList<Integer> likeClassList, String trainOut,
			String trainOutClass) {
		int forwardSize = forwardClassList.size();
		int commentSize = commentClassList.size();
		int likeSize = likeClassList.size();
		BufferedReader br=null;
		PrintWriter pw = null;
		String line = null;
		String []  count= null;
		try {
			br = RawIO.openReader(trainOut);
			pw = RawIO.openWriter(trainOutClass);
			while((line=br.readLine()) != null){
				line = line.trim();
				if(!line.equals("")){
					count = line.split("\t");
					int forwardCount = Integer.parseInt(count[3]);
					int commentCount = Integer.parseInt(count[4]);
					int likeCount = Integer.parseInt(count[5]);
					int forwardClass = classifyClass(forwardCount,forwardClassList, forwardSize);
					int commentClass = classifyClass(commentCount,commentClassList, commentSize);
					int likeClass =  classifyClass(likeCount,likeClassList, likeSize);
					pw.print(line);
					pw.print("\t");
					pw.print(forwardClass);
					pw.print("\t");
					pw.print(commentClass);
					pw.print("\t");
					pw.print(likeClass);
					pw.print("\n");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			RawIO.close(br);
			RawIO.close(pw);
		}
	}
	
	static int classifyClass(int c,ArrayList<Integer> classList, int classSize){
		int begin = 0;
		int end = classSize - 1;
		int middle = 0;
		int target = 0;
		while(begin <= end){
			middle = (begin + end) / 2;
			if( c > classList.get(middle)){
				begin = middle + 1;
				target = middle;
			}else if(c < classList.get(middle)){
				end = middle-1;
				target  = end;
			}else{
				target = middle;
				break;
			}
		}
		return classList.get(target);
	}

	static ArrayList<Integer> buildListFromFile(String filename){
    	BufferedReader br=null;
    	ArrayList<Integer> s = null;
		String line = null;
		try {
			 br = RawIO.openReader(filename);
			s = new ArrayList<Integer>();
			while((line=br.readLine()) != null){
				line = line.trim();
				if(!line.equals("")){
					s.add(Integer.parseInt(line));
				}
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			RawIO.close(br);
		}
    }
}
