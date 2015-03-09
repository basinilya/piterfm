package ru.piter.fm.aac;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import android.util.Log;

public class FixHeaderProxy extends Thread {

    private static final String Tag = "PiterFMPlayer";

    private ServerSocket srv;
    private StreamerUtil b = new StreamerUtil();

    public FixHeaderProxy() throws IOException {
        srv = new ServerSocket();
        srv.bind(new InetSocketAddress("127.0.0.1", 0)); //$NON-NLS-1$
        setDaemon(true);
        start();
    }

    public String getUrl() {
        return "http://127.0.0.1:" + srv.getLocalPort();
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
            final String funcname = "RequestProcessor,run";
            try {
                processRequest();
            } catch (Exception e) {
                Log.d(Tag, funcname + "," + e.toString());
                //e.printStackTrace();
                //throw new RuntimeException(e);
            }
        }

        @SuppressWarnings("resource")
        public void processRequest() throws Exception {
            byte buf[] = new byte[1000];
    
            InputStream proxyIn = null;
            OutputStream realOut = null;
            Socket realSock = null;
    
            try {
                proxyIn = new BufferedInputStream(proxySock.getInputStream());
                
                String s;
    
                String h_get = readCRLFLine(proxyIn);
                String h_host = readCRLFLine(proxyIn);
    
                String[] h_get_parts = h_get.split(" ", 3);
                String[] parts = h_get_parts[1].split("/");
                String stationId = parts[1];
                String timestamp = parts[2];
    
                String realUrl = b.getStreamUrl(stationId, Long.parseLong(timestamp));
                int i = realUrl.indexOf('/', 7);
    
                s = realUrl.substring(i);
                h_get = h_get_parts[0] + " " + s + " " + h_get_parts[2];
    
                String realhostport = realUrl.substring(7, i);
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
            final String funcname = "RequestProcessor,run";
            try {
                processResponse();
            } catch (Exception e) {
                Log.d(Tag, funcname + "," + e.toString());
                //e.printStackTrace();
                //throw new RuntimeException(e);
            }
        }
        
        public void processResponse() throws Exception {
            byte buf[] = new byte[1000];
            
            InputStream realIn = null;
            OutputStream proxyOut = null;
    
            try {
                proxyOut = new BufferedOutputStream(proxySock.getOutputStream());
                realIn = new BufferedInputStream(realSock.getInputStream());
                
                String h_icy200 = readCRLFLine(realIn);
                int i = h_icy200.indexOf(' ');
                h_icy200 = "HTTP/1.0" + h_icy200.substring(i);

                writeCRLFLine(proxyOut, h_icy200);

                int len;
                while (0 < (len = realIn.read(buf))) {
                    proxyOut.write(buf, 0, len);
                }            
            } finally {
                try { if (proxyOut != null) proxyOut.close(); } catch (Exception e) {  }
                try { if (realIn != null) realIn.close(); } catch (Exception e) {  }

                try { if (realSock != null) realSock.close(); } catch (Exception e) {  }
                try { if (proxySock != null) proxySock.close(); } catch (Exception e) {  }
                
            }
        }
    }
    

    /*
    
    new Thread() {
        public void run() {
            try {
                processResponse(sock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        };
    }.start();

    
    


}

public void processResponse(Socket sock) throws Exception {
    byte buf[] = new byte[1000];
    String http200str = "HTTP/1.1 200 OK\r\n\r\n";
    byte[] http200bytes = http200str.getBytes("iso-8859-1");
}

*/
}
