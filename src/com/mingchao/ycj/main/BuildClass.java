package com.mingchao.ycj.main;

public class BuildClass {
	public static void main(String[] args) {
//		System.out.println( (int)5.5 );
		build();
		
	}
	static void build(){
		int fr = 0;
		double rate = 1.2;
		double ext = 0.6;
		int predict;
		while(fr >=0 && fr < Integer.MAX_VALUE){
			predict = fr;
			System.out.println(predict);
			fr =  crtNr(fr,rate,ext);
		}
	}
	static int crtNr(int fr,double rate,double ext){
		double dr = rate*fr + ext;
		int nr;
		int ir = (int)dr;
		if( ir==0 | (dr - ir) != 0){
			nr = ir + 1;
		}else{
			nr = ir;
		}
		return nr;
	}
}
