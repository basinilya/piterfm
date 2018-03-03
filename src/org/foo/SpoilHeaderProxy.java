package org.foo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Logger;

public class SpoilHeaderProxy extends Thread {

	private static final Logger log = Logger.getLogger(SpoilHeaderProxy.class.getName());	

	private ServerSocket srv;

    public void stop2() throws IOException {
    	log.info("FixHeaderProxy destroy");
    	srv.close();
    }

    public SpoilHeaderProxy() throws IOException {
    	log.info("FixHeaderProxy create");
        srv = new ServerSocket();
        srv.bind(new InetSocketAddress(8080));
        setDaemon(true);
        start();
    }

    public void run() {
        try {
            for(;;) {
                try {
                    final Socket sock = srv.accept();
                    RequestProcessor requestProcessor = new RequestProcessor();
                    requestProcessor.proxySock = sock;
                    requestProcessor.start();
                } catch (SocketException e) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readCRLFLine(InputStream in) throws IOException {
        StringBuilder line = new StringBuilder(80); // Typical line length
        while (true) {
            int nextByte = in.read();
            switch (nextByte) {
                case -1:
                    if (line.length() == 0) {
                        return null;
                    }
                    return line.toString();
                case (byte) '\r':
                    in.read();
                    return line.toString();
                case (byte) '\n':
                    return line.toString();
                default:
                    line.append((char) nextByte);
            }
        }
    }

    private static byte[] crlf = new byte[] { '\r' , '\n' };

    public static void writeCRLFLine(OutputStream out, String s) throws IOException {
        out.write(s.getBytes("iso-8859-1"));
        out.write(crlf);
    }

    private class RequestProcessor extends Thread {

        public Socket proxySock;
        
        @Override
        public void run() {
            try {
                processRequest();
            } catch (Exception e) {
            }
        }

        @SuppressWarnings("resource")
        public void processRequest() throws Exception {
            byte buf[] = new byte[8192];
    
            InputStream proxyIn = null;
            OutputStream realOut = null;
            Socket realSock = null;
    
            try {
                proxyIn = new BufferedInputStream(proxySock.getInputStream());
                
                String s;
    
                String h_get = readCRLFLine(proxyIn);
                if (h_get == null) {
                    //Log.w(Tag, funcname + ",instant EOF in MediaPlayer http request");
                    return;
                }
                String h_host = readCRLFLine(proxyIn);

                int i;

                String realhostport = "127.0.0.1:8081";
                h_host = "Host: " + realhostport;
    
                int port = 80;
                i = realhostport.indexOf(':');
                if (i != -1) {
                    port = Integer.parseInt(realhostport.substring(i+1));
                    realhostport = realhostport.substring(0, i);
                }
                realSock = new Socket(realhostport, port);
       
                realOut = new BufferedOutputStream(realSock.getOutputStream());
    
                writeCRLFLine(realOut, h_get);
                writeCRLFLine(realOut, h_host);
    
                ResponseProcessor responseProcessor = new ResponseProcessor();
                responseProcessor.proxySock = proxySock;
                responseProcessor.realSock = realSock;
                
                realSock = null;
                proxySock = null;
                
                responseProcessor.start();
                
                int len;
                while (0 < (len = proxyIn.read(buf))) {
                    realOut.write(buf, 0, len);
                    realOut.flush();
                }
            } finally {
                try { if (proxyIn != null) proxyIn.close(); } catch (Exception e) {  }
                try { if (realOut != null) realOut.close(); } catch (Exception e) {  }

                try { if (realSock != null) realSock.close(); } catch (Exception e) {  }
                try { if (proxySock != null) proxySock.close(); } catch (Exception e) {  }
                
            }
        }
    }

    private class ResponseProcessor extends Thread {

        public Socket proxySock;
        public Socket realSock;
        
        @Override
        public void run() {
            try {
                processResponse();
            } catch (Exception e) {
            }
        }

        @SuppressWarnings("resource")
        public void processResponse() throws Exception {
            byte buf[] = new byte[8192];
            
            InputStream realIn = null;
            OutputStream proxyOut = null;
    
            try {
                proxyOut = proxySock.getOutputStream();
                proxyOut = new BufferedOutputStream(proxyOut);
                realIn = new BufferedInputStream(realSock.getInputStream());

                String s;

                String h_icy200 = readCRLFLine(realIn);
                int i = h_icy200.indexOf(' ');
                if (h_icy200.startsWith("200 ", i + 1)) {
                    s = "ICY" + h_icy200.substring(i);
                    //Log.d(Tag, funcname + ",replacing '" + h_icy200 + "' with '" + s + " in response");
                    h_icy200 = s;
                }
                writeCRLFLine(proxyOut, h_icy200);

                int len;

                len = realIn.read(buf);
                if (0 < len) {
                    proxyOut.write(buf, 0, len);

                    proxyOut.flush();
                    proxyOut = proxySock.getOutputStream();

                    while (0 < (len = realIn.read(buf))) {
                        proxyOut.write(buf, 0, len);
                    }
                }
            } finally {
                try { if (proxyOut != null) proxyOut.close(); } catch (Exception e) {  }
                try { if (realIn != null) realIn.close(); } catch (Exception e) {  }

                try { if (realSock != null) realSock.close(); } catch (Exception e) {  }
                try { if (proxySock != null) proxySock.close(); } catch (Exception e) {  }
            }
        }
    }
}
