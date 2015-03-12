/**
 * 
 */
package ru.piter.fm.aac;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import android.util.Log;

/**
 * @author Ilya Basin
 * 
 */
public class StreamerUtil {

    private static final String Tag = "PiterFMPlayer";

    private String cachedTemplate;
    private long nanos;
    private static final long EXPIRE_NANOS = 1800 * 1000 * 1000000L;

    public synchronized String getStreamUrl(String stationId, long timestamp) throws Exception {
        final String funcname = "getStreamUrl";
        if ("".length() == 10) {
            return "http://192.168.2.146:8080/piterfm-test-server/MyServlet?throttlekbs=8&boostkb=0";
        }
        
        String secondsFloor = Long.toString(timestamp / 1000);
        String seconds = secondsFloor + "." + Long.toString(timestamp % 1000);

        mylabel:
        if (cachedTemplate == null || System.nanoTime() - nanos > EXPIRE_NANOS) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            String s = formatXmlUrl(stationId, secondsFloor);
            Document dom = builder.parse(s);
            for (Node node2 = dom.getDocumentElement().getFirstChild();node2 != null; node2 = node2.getNextSibling()) {
                if (node2.getNodeType() == Node.ELEMENT_NODE && "streamers".equals(node2.getNodeName())) {
                    for (Node node3 = node2.getFirstChild();node3 != null; node3 = node3.getNextSibling()) {
                        if (node3.getNodeType() == Node.ELEMENT_NODE && "streamer".equals(node3.getNodeName())) {
                            cachedTemplate = ((Element)node3).getAttribute("url");
                            Log.d(Tag, funcname + ",template = " + cachedTemplate + " , channelId = " + stationId + " , timestamp = " + timestamp);
                            cachedTemplate = cachedTemplate.replace("format=flv", "format=aac");
                            nanos = System.nanoTime();
                            break mylabel;
                        }
                    }
                }
            }
            throw new Exception("stream url not found");
        }

        String streamUrl = cachedTemplate.replace("%station_id", stationId);
        streamUrl = streamUrl.replace("%timestamp", seconds);
        return streamUrl;    
    }

    private String formatXmlUrl(String channelId, String secondsFloor) {
        final String funcname = "formatXmlUrl";
        String rnd = Double.toString(Math.random()).replace(".", "%2E");
        String s = "http://www.moskva.fm/player_xml.html?startat=" + secondsFloor + "&type=full&rnd=" + rnd + "&v=3&time=" + secondsFloor + "&station=" + channelId;
        Log.d(Tag, funcname + ",xmlurl = " + s);
        return s;
    }
    /*
    public static void main(String[] args) throws Exception {
        System.out.println(new StreamerUtil().getStreamUrl("4019", 1425399170844L));
    }
    */
}
