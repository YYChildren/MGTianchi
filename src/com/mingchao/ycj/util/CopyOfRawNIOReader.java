package com.mingchao.ycj.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CopyOfRawNIOReader {
	public static final int BUFFER_SIZE = 8192;
	public static final String charset = "UTF-8";
	private int defaultExpectedLineLength = 88;
	private boolean skipLF = false;
	private CharBuffer charBuffer;
	private FileInputStream in;
	private char[] cb;
	private int len;
	private int nChars;
	private int nextChar;
	private int offset;

	public CopyOfRawNIOReader(String path) throws IOException {
		this(new File(path));
	}

	public CopyOfRawNIOReader(File file) throws IOException {
		in = new FileInputStream(file);
		FileChannel channel = in.getChannel();
		MappedByteBuffer buff = channel.map(FileChannel.MapMode.READ_ONLY, 0,
				channel.size());
		Charset charset = Charset.forName("UTF-8");
		CharsetDecoder decoder = charset.newDecoder();
		charBuffer = decoder.decode(buff);
		cb = new char[BUFFER_SIZE];
		len = charBuffer.length();
		nChars = nextChar = 0;
	}

	public String readLine() throws IOException {
		return readLine(false);
	}

	String readLine(boolean ignoreLF) throws IOException {
		StringBuilder s = null;
		int startChar;

		// synchronized (this) {
		ensureOpen();
		boolean omitLF = ignoreLF || skipLF;

		for (;;) {

			if (nextChar >= nChars)
				fill();
			if (nextChar >= nChars) { /* EOF */
				if (s != null && s.length() > 0)
					return s.toString();
				else
					return null;
			}
			boolean eol = false;
			char c = 0;
			int i;

			/* Skip a leftover '\n', if necessary */
			if (omitLF && (cb[nextChar] == '\n'))
				nextChar++;
			skipLF = false;
			omitLF = false;

			charLoop: for (i = nextChar; i < nChars; i++) {
				c = cb[i];
				if ((c == '\n') || (c == '\r')) {
					eol = true;
					break charLoop;
				}
			}

			startChar = nextChar;
			nextChar = i;

			if (eol) {
				String str;
				if (s == null) {
					str = new String(cb, startChar, i - startChar);
				} else {
					s.append(cb, startChar, i - startChar);
					str = s.toString();
				}
				nextChar++;
				if (c == '\r') {
					skipLF = true;
				}
				return str;
			}

			if (s == null)
				s = new StringBuilder(defaultExpectedLineLength);
			s.append(cb, startChar, i - startChar);
		}
		// }
	}

	private void fill() throws IOException {
		int n = len - offset;
		if (n > BUFFER_SIZE) {
			charBuffer.get(cb, 0, BUFFER_SIZE);
			n = BUFFER_SIZE;
		} else if (n > 0) {
			charBuffer.get(cb, 0, n);
		} else {
			return;
		}
		offset += BUFFER_SIZE;
		if (n > 0) {
			nChars = n;
			nextChar = 0;
		}
	}

	private void ensureOpen() throws IOException {
		if (in == null)
			throw new IOException("Stream closed");
	}

	public void close() {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		File file = new File("E:/WeiboPred/train2/pred_src.tsv");

		long begin = System.currentTimeMillis();

		CopyOfRawNIOReader rnio = new CopyOfRawNIOReader(file);
		String line = null;
		while ((line = rnio.readLine()) != null) {
			System.out.println(line);
		}
		rnio.close();
		long end = System.currentTimeMillis();
		System.out.println("time is:" + (end - begin));
		//java.io.BufferedReader br = null;
	}
}
