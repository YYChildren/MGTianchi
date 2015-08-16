package com.mingchao.ycj.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.hit.ir.ltp4j.NER;
import edu.hit.ir.ltp4j.Postagger;
import edu.hit.ir.ltp4j.Segmentor;

public class SentenceHandler {
	private static AnalyzePos defaultAnalyzePos = AnalyzePos.ner;
	private int size;
	private String sentence;
	private List<String> segs;// 分词
	private List<String> tags;// 命名实体识别
	private List<String> ners;// 实体标注
	
	public static enum AnalyzePos {
		seg, // 分词
		tag, // 命名实体识别
		ner, // 实体标注
	}
	public SentenceHandler analyze(String str) throws Exception {
		return analyze(str, defaultAnalyzePos);
	}

	public SentenceHandler analyze(String str, AnalyzePos analyzePos) throws Exception {
		str = str.trim();
		if(str.equals("")){
			throw new Exception("The string to be ananlyze is empty after trim()");
		}
		clean();
		this.sentence = str;
		switch (analyzePos) {
		case seg:
			segs = new ArrayList<String>();
			Segmentor.segment(str, segs);
			break;
		case tag:
			segs = new ArrayList<String>();
			Segmentor.segment(str, segs);
			tags = new ArrayList<String>();
			Postagger.postag(segs, tags);
			break;
		case ner:
			segs = new ArrayList<String>();
			Segmentor.segment(str, segs);
			tags = new ArrayList<String>();
			Postagger.postag(segs, tags);
			ners = new ArrayList<String>();
			NER.recognize(segs, tags, ners);
			break;
		default:
			throw new Exception("No step " + analyzePos);

		}
		size = segs.size();
		return this;
	}

	private void clean() {
		segs=null;
		tags=null;
		ners=null;
	}

	public int getSize() {
		return size;
	}

	public String getSentence() {
		return sentence;
	}

	public List<String> getSegs() {
		return segs;
	}

	public List<String> getTags() {
		return tags;
	}

	public List<String> getNers() {
		return ners;
	}

	@Override
	public int hashCode() {
		return this.sentence.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SentenceHandler other = (SentenceHandler) obj;
		return Objects.equals(this.sentence, other.sentence);
	}

	@Override
	public String toString() {
		return this.sentence;
	}
}
