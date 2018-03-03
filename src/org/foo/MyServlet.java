package org.foo;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private FileInputStream openFile() throws IOException {
		return new FileInputStream(getServletContext().getRealPath("/sample3.aac"));
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
