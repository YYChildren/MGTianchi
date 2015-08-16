/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mingchao.ycj.util;

import edu.hit.ir.ltp4j.NER;
import edu.hit.ir.ltp4j.Postagger;
import edu.hit.ir.ltp4j.Segmentor;

public final class SHFactory {
	private static String cwsModel;
	private static String posModel;
	private static String nerModel;
	private static Boolean isLoaded = false;
	private static SHFactory shf = null;

	public static SHFactory getInstance() {
		if (shf == null) {
			syncInit();
		}
		return shf;
	}
	
	public static SHFactory getInstance(String cwsModel,String posModel,String nerModel) {
		if (shf == null) {
			SHFactory.cwsModel = cwsModel;
			SHFactory.posModel = posModel;
			SHFactory.nerModel = nerModel;
			syncInit();
		}
		return shf;
	}

	public static void closeInstance() {
		if (shf != null) {
			destroy();
		}
	}

	public SentenceHandler buildHandler() {
		return new SentenceHandler();
	}

	private synchronized static void syncInit() {
		if (shf == null) {
			// cwsModel = GlobalArgs.getCwsModelPath();
			// posModel = GlobalArgs.getPosModelPath();
			// nerModel = GlobalArgs.getNerModelPath();
			if (isLoaded) {
				return;
			}
			if (Segmentor.create(cwsModel) < 0) {
				System.err.println("cwsModel load failed");
			} else {
				System.out.println("cwsModel loaded");
			}
			if (Postagger.create(posModel) < 0) {
				System.err.println("posModel load failed");
			} else {
				System.out.println("posModel loaded");
			}
			if (NER.create(nerModel) < 0) {
				System.err.println("nerModel load failed");
			} else {
				System.out.println("nerModel loaded");
			}
			isLoaded = true;
			shf = new SHFactory();
		}
	}

	private SHFactory() {
	}

	@Override
	protected void finalize() throws java.lang.Throwable {
		destroy();
		super.finalize();
	}

	private synchronized static void destroy() {
		if (shf != null) {
			if (!isLoaded) {
				return;
			}
			Segmentor.release();
			Postagger.release();
			NER.release();
			isLoaded = false;
			shf = null;
		}
	}
}
