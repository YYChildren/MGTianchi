package com.mingchao.ycj.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectIO {
	public static ObjectInputStream openInputStream(String path) throws FileNotFoundException, IOException{
		return new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(path))));
	}
	public static ObjectOutputStream openOutputStream(String path) throws FileNotFoundException, IOException{
		return new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(path))));
	}
	
	public static void close(ObjectInputStream ois){
		try {
			ois.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void close(ObjectOutputStream oos){
		try {
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
