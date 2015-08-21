package com.mingchao.ycj.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Tool {
	
	public static Set<String> buildSetFromFile(String filename){
    	BufferedReader br=null;
		Set<String> s = null;
		String line = null;
		try {
			 br = new BufferedReader(new FileReader(filename));
			s = new HashSet<String>();
			while((line=br.readLine()) != null){
				line = line.trim();
				if(!line.equals("")){
					s.add(line);
				}
			}
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
}
