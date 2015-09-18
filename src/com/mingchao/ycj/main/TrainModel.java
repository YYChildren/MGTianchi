package com.mingchao.ycj.main;

public class TrainModel {
	public static void main(String[] args) {
		preprocess();
		addClassTail();
		mining();
		mining2();
	}
	
	private static void preprocess(){
		TianChiSH.main(new String[0]);// 分词词性标注
	}
	
	private static void addClassTail(){
		AddClassTail.main(new String[0]);
	}

	private static void mining() {
	}

	private static void mining2() {
	}
}
