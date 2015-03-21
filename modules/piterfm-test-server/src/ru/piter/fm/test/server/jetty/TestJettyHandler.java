package ru.piter.fm.test.server.jetty;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import ru.piter.fm.test.server.TestHandler;

public class TestJettyHandler extends AbstractHandler {

	private final TestHandler handler = new TestHandler() {
		@Override
		protected String getRealPath(String s) {
			return new File(".", s).getAbsolutePath();
		}
	};

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		handler.doGet(request, response);
	}

}
