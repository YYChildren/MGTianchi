package com.mingchao.ycj.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileCombine {
	public static void combine(List<String> pathList, String fileoutput)
			throws IOException {
		ArrayList<String> a = new ArrayList<String>();
		for (String path : pathList) {
			BufferedReader br = RawIO.openReader(path);
			String line = null;
			while ((line = br.readLine()) != null) {
				a.add(line);
			}
			RawIO.close(br);
		}
		Collections.sort(a);
		PrintWriter pw = RawIO.openWriter(fileoutput);
		a.forEach(line -> pw.print(line + "\n"));
		RawIO.close(pw);
	}

	public static void combine2(String forwardSrc, String commentSrc,
			String likeSrc, String allOut) throws IOException {
		BufferedReader fi = RawIO.openReader(forwardSrc);
		BufferedReader ci = RawIO.openReader(commentSrc);
		BufferedReader li = RawIO.openReader(likeSrc);
		PrintWriter pw = RawIO.openWriter(allOut);
		String frecord = null;
		String crecord = null;
		String lrecord = null;
		while ((frecord = fi.readLine()) != null) {
			crecord = ci.readLine();
			lrecord = li.readLine();
			pw.print(frecord + "," + crecord.split("\t")[2] + ","
					+ lrecord.split("\t")[2] + "\n");
		}
		pw.flush();
		RawIO.close(pw);
		RawIO.close(li);
		RawIO.close(ci);
		RawIO.close(fi);
	}
}
