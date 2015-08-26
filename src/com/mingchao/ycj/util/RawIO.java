package com.mingchao.ycj.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class RawIO {
	public static final String FILE_ENCODE = "UTF-8";

	public static BufferedReader openReader(String path) throws FileNotFoundException {
		try {
			return new BufferedReader(new InputStreamReader(new FileInputStream(
					new File(path)), FILE_ENCODE));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void closeReader(BufferedReader br){
		if (br != null)
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public static PrintWriter openWriter(String path) throws FileNotFoundException{
		try {
			return new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(new File(path)),FILE_ENCODE)));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} 
	}

	public static void closeWriter(PrintWriter pw) {
		if (pw != null)
			pw.close();
	}
}
