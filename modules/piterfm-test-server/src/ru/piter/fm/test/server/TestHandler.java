package ru.piter.fm.test.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class TestHandler {

	protected abstract String getRealPath(String s);

	private FileInputStream openFile() throws IOException {
		String path = getRealPath("./") + "/../../sample.aac";
		return new FileInputStream(path);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FileInputStream in = openFile();
		try {
			OutputStream out = response.getOutputStream();
	
			response.setDateHeader("Date", System.currentTimeMillis());
			response.setHeader("icy-notice1", "<BR>This stream requires <a href=\"http://www.winamp.com/\">Winamp</a><BR>");
			response.setHeader("icy-notice2", "Moskva audio streamer");
			response.setHeader("icy-name", "Moskva audio streamer");
			response.setContentType("audio/aacp");
			String s = request.getParameter("length");
			long maxlength = -1;
			if (s != null) {
				int i = Integer.parseInt(s);
				response.setContentLength(i);
				maxlength = i;
			}
			response.setHeader("icy-pub", "1");
			response.setHeader("icy-br", "32");
	
			double desiredBps = 4096;
			s = request.getParameter("throttlekbs");
			if (s != null) {
				desiredBps = Double.parseDouble(s) * 1024;
			}
			
			Throttler thr = new Throttler(desiredBps);
	
			byte[] buf2 = new byte[8192];
	
			int len;
			long total = 0;

			boolean maxlengthReached = false;
			for(;;) {
	
				int buflen = Math.min(buf2.length, thr.bestBufsz());
	
				len = in.read(buf2, 0, buflen);
				
				if (len == -1) {
					in.close();
					in = openFile();
					continue;
				}

				if (maxlength != -1 && (total + len) >= maxlength) {
				        len = (int)(maxlength - total);
				        maxlengthReached = true;
				}				

				out.write(buf2, 0, len);
				out.flush();

				total += len;
				if (maxlengthReached) break;

				thr.acquire(len);
			}
		} finally {
			in.close();
		}
	}

}
