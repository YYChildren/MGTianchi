package com.mingchao.ycj.mining2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.mingchao.ycj.util.ObjectIO;

public class ModelSer {
	public static void writeModel( 
			//forward相关信息
			HashMap<Integer,Double> mFClsCount,
			HashMap<Integer,HashMap<String,Double>> mFClsMUidCount,
			HashMap<Integer,HashMap<String,Double>> mFClsMWordCount,
			
			//comment 相关信息
			HashMap<Integer,Double> mCClsCount,
			HashMap<Integer,HashMap<String,Double>> mCClsMUidCount,
			HashMap<Integer,HashMap<String,Double>> mCClsMWordCount,
			
			//like 相关信息
			HashMap<Integer,Double> mLClsCount,
			HashMap<Integer,HashMap<String,Double>> mLClsMUidCount,
			HashMap<Integer,HashMap<String,Double>> mLClsMWordCount,
			
			String path){
		ArrayList<Object> mapList = new ArrayList<>(9);
		mapList.add(mFClsCount);
		mapList.add(mFClsMUidCount);
		mapList.add(mFClsMWordCount);
		mapList.add(mCClsCount);
		mapList.add(mCClsMUidCount);
		mapList.add(mCClsMWordCount);
		mapList.add(mLClsCount);
		mapList.add(mLClsMUidCount);
		mapList.add(mLClsMWordCount);
		writeObject(mapList,path);
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Object> readModel(String path){
		return (ArrayList<Object>) readObject(path);
	}
	
	public static void writeObject(Object o,String path){
		ObjectOutputStream oos = null;
		try {
			  oos = ObjectIO.openOutputStream(path);
			  System.out.println("Writing object");
			  oos.writeObject(o);
			  System.out.println("Wroten object");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			ObjectIO.close(oos);
		}
	}
	public static Object readObject(String path){
		ObjectInputStream oos = null;
		try {
			System.out.println("Reading object");
			oos = ObjectIO.openInputStream(path);
			return oos.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}finally{
			ObjectIO.close(oos);
			System.out.println("Read object");
		}
	}
}
