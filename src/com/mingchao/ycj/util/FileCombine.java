package com.mingchao.ycj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileCombine {
	public static void  combine(String dir,String regex,String fileoutput) throws IOException{
		List<String> a = new ArrayList<String>();
		FilenameFilter filter = new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(regex);
			}
			
		};
		File d = new File("dir");
		if(d.isDirectory()){
			for(File f : d.listFiles(filter)){
				BufferedReader br = RawIO.openReader(f);
				String line=null;
				while((line = br.readLine())!=null){
					a.add(line);
				}
				br.close();
			}
		}
		Collections.sort(a);
		PrintWriter pw = RawIO.openWriter(fileoutput);
		a.forEach(line -> pw.print(line+"\n"));
	}
	
}
