package ru.piter.fm.test.server.jetty;

import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Server;

import ru.piter.fm.test.server.SpoilHeaderProxy;

public class Main {

	public static void main(String[] args) throws Exception {

		Server server = new Server(0);

		server.setHandler(new TestJettyHandler());

		server.start();

		NetworkConnector conn = (NetworkConnector)server.getConnectors()[0];
		int port = conn.getLocalPort();

		new SpoilHeaderProxy(port);
		server.join();
	}
}
