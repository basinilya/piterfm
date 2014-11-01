package ru.piter.fm.util;
import android.annotation.SuppressLint;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * @author Ilya Basin
 *
 */
@SuppressLint("NewApi")
public class ChannelsDownloader {

    public static void main(String[] args) throws Exception {
        final String URLS[]  = { "http://www.piter.fm/stations/order:frequency" , "http://www.moskva.fm/stations/order:frequency" } ;
        final String FILES[]  = { "PiterFM.xml", "MoskvaFM.xml" } ;

        DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

        for (int iURL = 0; iURL < URLS.length; iURL++) {
            File docfile = new File("assets/xml/" + FILES[iURL]);
            Document filedoc = domBuilder.parse(docfile);
            URL stationsPage = new URL(URLS[iURL]);
            new ChannelsDownloader().doStuff(filedoc, docfile, stationsPage);
        }
    }

    public ChannelsDownloader() throws Exception {
        //
    }

    private static final String ST_ID = "id";
    private static final String ST_NAME = "name";
    private static final String ST_RANGE = "range";
    private static final String ST_LOGO = "logo";
    private static final String ST_TRANSLATION = "translation";

    //private static final String URLS[]  = { new File("piter.html").toURI().toString(), new File("moskva.html").toURI().toString() } ;

    private boolean xmlChanged;

    public void doStuff(Document existingDoc, File outputFile) throws Exception {
        String stationsPage;
        if (outputFile.getPath().endsWith("/PiterFM.xml")) {
            stationsPage = "http://www.piter.fm/stations/order:frequency";
        } else if (outputFile.getPath().endsWith("/MoskvaFM.xml")) {
            stationsPage = "http://www.moskva.fm/stations/order:frequency";
        } else {
            throw new Exception("unknowd radio");
        }
        doStuff(existingDoc, outputFile, new URL(stationsPage));
    }

    public void doStuff(Document existingDoc, File outputFile, URL stationsPage) throws Exception {
        Pattern p = Pattern.compile("(?s).*a href=\"(http://[^/]*/play/([^/]*)/[^\"]*)\".*");
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression xpathNodes = xpath.compile("//div[div[2]/comment() and div/a/img[contains(@src, '/165x165/')]]");
        XPathExpression xpathLogo = xpath.compile("./div[1]/a/img/@src");
        XPathExpression xpathRange = xpath.compile("./div[2]/h4/small");
        XPathExpression xpathName = xpath.compile("./div[2]/h4/a/b");
        XPathExpression xpathComment = xpath.compile("./div[2]/comment()");
        XPathExpression xpathExistingRangeList = xpath.compile("./channel/range");
        XPathExpression xpathFirstChannel = xpath.compile("./channel[1]");
        XPathExpression xpathChannelsNode = xpath.compile("/channels");

        Element channelsNode = (Element)xpathChannelsNode.evaluate(existingDoc, XPathConstants.NODE);

        Node textNode = null;
        Element sampleChannel = (Element)xpathFirstChannel.evaluate(channelsNode, XPathConstants.NODE);
        if (sampleChannel == null) {
            sampleChannel = existingDoc.createElement("channel");
            sampleChannel.appendChild(existingDoc.createElement(ST_ID));
            sampleChannel.appendChild(existingDoc.createElement(ST_NAME));
            sampleChannel.appendChild(existingDoc.createElement(ST_RANGE));
            sampleChannel.appendChild(existingDoc.createElement(ST_LOGO));
            sampleChannel.appendChild(existingDoc.createElement(ST_TRANSLATION));
        } else {
            textNode = sampleChannel.getNextSibling();
            sampleChannel = (Element)sampleChannel.cloneNode(true);
        }
        if (textNode == null) {
            textNode = existingDoc.createTextNode("");
        }
        //filedoc.getFirstChild().getFirstChild();
        xmlChanged = false;
        Document htmldoc = parse(stationsPage);

        HashSet<String> seenRanges = new HashSet<String>();

        NodeList nl = (NodeList)xpathNodes.evaluate(htmldoc, XPathConstants.NODESET);
        int nNodes = nl.getLength();
        if (nNodes == 0) {
            throw new Exception("html parse failed");
        }
        for (int i = 0; i < nNodes; i++) {
            Node thediv = nl.item(i);

            String stLogo = evaluate(xpathLogo, thediv);
            String stRange = evaluate(xpathRange, thediv);
            String stName = evaluate(xpathName, thediv);

            String comment = evaluate(xpathComment, thediv);
            Matcher m = p.matcher(comment);
            if (!m.matches()) {
                throw new Exception("html parse failed");
            }
            String stTranslation = m.group(1);
            String stId = m.group(2);

            seenRanges.add(stRange);

            Element channel = (Element)xpath.evaluate(byRange(stRange), existingDoc,  XPathConstants.NODE);
            if (channel == null) {
                channel = (Element)sampleChannel.cloneNode(true);
                channelsNode.appendChild(channel);
                channelsNode.appendChild(textNode.cloneNode(true));
                xmlChanged = true;
            }
            setTag(channel, ST_ID, stId);
            setTag(channel, ST_NAME, stName);
            setTag(channel, ST_RANGE, stRange);
            setTag(channel, ST_LOGO, stLogo);
            setTag(channel, ST_TRANSLATION, stTranslation);
        }
        nl = (NodeList)xpathExistingRangeList.evaluate(channelsNode, XPathConstants.NODESET);
        for (int i = 0, len = nl.getLength(); i < len; i++) {
            Node rangeNode = nl.item(i);
            String range = rangeNode.getTextContent();
            if (!seenRanges.contains(range)) {
                Node channelNode = rangeNode.getParentNode();
                channelNode.getParentNode().removeChild(channelNode);
                xmlChanged = true;
            }
        }
        
        if (xmlChanged) {
            DOMImplementationLS domImplementationLS =
                    (DOMImplementationLS) existingDoc.getImplementation().getFeature("LS","3.0");
            if (domImplementationLS != null) {
                LSOutput lsOutput = domImplementationLS.createLSOutput();
                FileOutputStream fos = new FileOutputStream(outputFile);
                try {
                    lsOutput.setByteStream(fos);
                    LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
                    lsSerializer.write(existingDoc, lsOutput);
                } finally {
                    try { fos.close(); } catch (Exception e) {}
                }
            } else {
                // fallback
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StreamResult output = new StreamResult(outputFile);
                DOMSource input = new DOMSource(existingDoc);
                transformer.transform(input, output);
            }
        }
    }
    
    private void setTag(Element channel, String tag, String val) {
        Node node = channel.getElementsByTagName(tag).item(0);
        if (!val.equals(node.getTextContent())) {
            node.setTextContent(val);
            xmlChanged  = true;
        }
    }

    private String byRange(String param) {
        return MessageFormat.format("/channels/channel[range = {0}]", escapeXpath(param));
    }

    private String escapeXpath(String param) {
        return "'" + param + "'";
    }

    private String evaluate(XPathExpression x, Node node) throws Exception {
        return x.evaluate(node).replaceAll("\u00a0", " "); // remove &nbsp;
    }

    public Document parse(URL u) throws Exception {
        CleanerProperties props = new CleanerProperties();

        props.setTranslateSpecialEntities(true);
        props.setTransResCharsToNCR(true);
        props.setOmitComments(false);

        TagNode tagNode = new HtmlCleaner(props).clean(u);

        Document doc = new DomSerializer(props).createDOM(tagNode);
        return doc;
    }
}
