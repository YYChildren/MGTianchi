package com.mingchao.ycj.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ExecutorX {
	private ExecutorX(){}
	private static class ExecutorXIn{
		private static ThreadPoolExecutor exec=(ThreadPoolExecutor) Executors.newFixedThreadPool(16);  
	}
	public static  ThreadPoolExecutor getInstance(){
		return ExecutorXIn.exec;
	}
}
