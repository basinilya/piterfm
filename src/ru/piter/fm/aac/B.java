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
public class B {

    private static final String Tag = "PiterFMPlayer";

    public String doIt(String channelId, long timestamp) throws Exception {
        final String funcname = "doIt";
        if ("".length() == 0) {
            return "http://192.168.2.146:8080/piterfm-test-server/sample.aac";
        }
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        String secondsFloor = Long.toString(timestamp / 1000);
        String seconds = secondsFloor + "." + Long.toString(timestamp % 1000);
        String s = formatXmlUrl(channelId, secondsFloor);
        Document dom = builder.parse(s);
        for (Node node2 = dom.getDocumentElement().getFirstChild();node2 != null; node2 = node2.getNextSibling()) {
            if (node2.getNodeType() == Node.ELEMENT_NODE && "streamers".equals(node2.getNodeName())) {
                for (Node node3 = node2.getFirstChild();node3 != null; node3 = node3.getNextSibling()) {
                    if (node3.getNodeType() == Node.ELEMENT_NODE && "streamer".equals(node3.getNodeName())) {
                        String streamUrl = ((Element)node3).getAttribute("url");
                        //Log.d(Tag, funcname + ",url = " + streamUrl + " , channelId = " + channelId + " , timestamp = " + timestamp);
                        streamUrl = streamUrl.replace("format=flv", "format=aac");
                        streamUrl = streamUrl.replace("%station_id", channelId);
                        streamUrl = streamUrl.replace("%timestamp", seconds);
                        return streamUrl;
                    }
                }
            }
        }
        throw new Exception("stream url not found");
    }

    private String formatXmlUrl(String channelId, String secondsFloor) {
        String rnd = Double.toString(Math.random()).replace(".", "%2E");
        String s = "http://www.moskva.fm/player_xml.html?startat=" + secondsFloor + "&type=full&rnd=" + rnd + "&v=3&time=" + secondsFloor + "&station=" + channelId;
        return s;
    }
    public static void main(String[] args) throws Exception {
        System.out.println(new B().doIt("4019", 1425399170844L));
    }
}
