package org.foo;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MyServletContextListener implements ServletContextListener {

	private SpoilHeaderProxy p;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			p = new SpoilHeaderProxy();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		if (p != null) {
			try {
				p.stop2();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
