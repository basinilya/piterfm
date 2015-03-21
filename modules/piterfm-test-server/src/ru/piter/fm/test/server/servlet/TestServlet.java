package ru.piter.fm.test.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ru.piter.fm.test.server.TestHandler;

public class TestServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final TestHandler handler = new TestHandler() {

		@Override
		protected String getRealPath(String s) {
			return getServletContext().getRealPath(s);
		}
	};

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		handler.doGet(req, resp);
	}
}
