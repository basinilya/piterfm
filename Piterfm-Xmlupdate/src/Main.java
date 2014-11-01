import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Attribute;
import javax.swing.text.html.HTML.Tag;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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
public class Main {

    public static void main(String[] args) throws Exception {
        new Main().doStuff();
    }

    public Main() throws Exception {
        //
    }

    private String ENCODING = "UTF-8";

    private static final String ST_ID = "id";
    private static final String ST_NAME = "name";
    private static final String ST_RANGE = "range";
    private static final String ST_LOGO = "logo";
    private static final String ST_TRANSLATION = "translation";

    private static final String URLS[]  = { "http://www.piter.fm/stations/order:frequency" , "http://www.moskva.fm/stations/order:frequency" } ;
    //private static final String URLS[]  = { new File("piter.html").toURI().toString(), new File("moskva.html").toURI().toString() } ;
    private static final String FILES[]  = { "PiterFM.xml", "MoskvaFM.xml" } ;

    private boolean xmlChanged;
    
    public void doStuff() throws Exception {
        String spattern = "(?s).*a href=\"(http://[^/]*/play/([^/]*)/[^\"]*)\".*";
        Pattern p = Pattern.compile(spattern);
        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression xpathNodes = xpath.compile("//div[div[2]/comment() and div/a/img[contains(@src, '/165x165/')]]");
        XPathExpression xpathLogo = xpath.compile("./div[1]/a/img/@src");
        XPathExpression xpathRange = xpath.compile("./div[2]/h4/small");
        XPathExpression xpathName = xpath.compile("./div[2]/h4/a/b");
        XPathExpression xpathComment = xpath.compile("./div[2]/comment()");
        XPathExpression xpathExistingRangeList = xpath.compile("./channel/range");
        XPathExpression xpathFirstChannel = xpath.compile("./channel[1]");
        XPathExpression xpathChannelsNode = xpath.compile("/channels");

        for (int iURL = 0; iURL < URLS.length; iURL++) {
            File docfile = new File("../assets/xml/" + FILES[iURL]);
            Document filedoc = domBuilder.parse(docfile);
            Element channelsNode = (Element)xpathChannelsNode.evaluate(filedoc, XPathConstants.NODE);

            Node textNode = null;
            Element sampleChannel = (Element)xpathFirstChannel.evaluate(channelsNode, XPathConstants.NODE);
            if (sampleChannel == null) {
                sampleChannel = filedoc.createElement("channel");
                sampleChannel.appendChild(filedoc.createElement(ST_ID));
                sampleChannel.appendChild(filedoc.createElement(ST_NAME));
                sampleChannel.appendChild(filedoc.createElement(ST_RANGE));
                sampleChannel.appendChild(filedoc.createElement(ST_LOGO));
                sampleChannel.appendChild(filedoc.createElement(ST_TRANSLATION));
            } else {
                textNode = sampleChannel.getNextSibling();
                sampleChannel = (Element)sampleChannel.cloneNode(true);
            }
            if (textNode == null) {
                textNode = filedoc.createTextNode("");
            }
            //filedoc.getFirstChild().getFirstChild();
            xmlChanged = false;
            Document htmldoc;
            InputStream in = new URL(URLS[iURL]).openStream();
            try {
                htmldoc = parse(new InputStreamReader(in, ENCODING));
            } finally {
                try { in.close(); } catch (IOException e ) {} 
            }

            HashSet<String> seenRanges = new HashSet<String>();

            NodeList nl = (NodeList)xpathNodes.evaluate(htmldoc, XPathConstants.NODESET);
            for (int i = 0, len = nl.getLength(); i < len; i++) {
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

                Element channel = (Element)xpath.evaluate(byRange(stRange), filedoc,  XPathConstants.NODE);
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
                        (DOMImplementationLS) filedoc.getImplementation().getFeature("LS","3.0");
                LSOutput lsOutput = domImplementationLS.createLSOutput();
                FileOutputStream fos = new FileOutputStream(docfile);
                lsOutput.setByteStream(fos);
                LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
                lsSerializer.write(filedoc, lsOutput);
                fos.close();
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
        return x.evaluate(node).replaceAll("\u00a0", " ");
    }

    private Document parse(InputStreamReader r) throws Exception {        
        ParserGetter kit = new ParserGetter();
        HTMLEditorKit.Parser parser = kit.getParser();
        HtmlToXml callback = new HtmlToXml();
        DOMResult domres = new DOMResult();
        Document doc = domBuilder.newDocument();
        domres.setNode(doc);
        callback.out = xmlOutputFactory.createXMLStreamWriter(domres);
        parser.parse(r, callback, true);
        r.close();
        return doc;
    }

    private DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    private XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
    private StreamResult xmlOutput = new StreamResult(new PrintWriter(System.out));
    private Transformer transformer = TransformerFactory.newInstance().newTransformer();
    {
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
    }

    private void printNodeList(NodeList nl) throws Exception {
        for (int i = 0, len = nl.getLength(); i < len; i++) {
            printNode(nl.item(i));
        }
    }

    private void printNode(Node node) throws Exception {
        transformer.transform(new DOMSource(node), xmlOutput);
    }
    
    private static class ParserGetter extends HTMLEditorKit {
        public HTMLEditorKit.Parser getParser() {
            return super.getParser();
        }
    }
}
class HtmlToXml extends HTMLEditorKit.ParserCallback {

    public XMLStreamWriter out;

    private void writeAttrs(MutableAttributeSet attributes) throws Exception {
        Enumeration<?> en = attributes.getAttributeNames();
        while (en.hasMoreElements()) {
            String attrName = en.nextElement().toString();
            Object attrKey = HTML.getAttributeKey(attrName);
            if (attrKey == null) {
                attrKey = attrName;
            }
            String attrVal = attributes.getAttribute(attrKey).toString();
            out.writeAttribute(attrName, attrVal);
            try {
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
    private void writeElement(Tag tag, MutableAttributeSet attributes) throws Exception {
        out.writeStartElement(tag.toString());
        writeAttrs(attributes);
    }

    @Override
    public void handleSimpleTag(Tag tag, MutableAttributeSet attributes, int position) {
        try {
            writeElement(tag, attributes);
            out.writeEndElement();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleStartTag(Tag tag, MutableAttributeSet attributes, int position) {
        try {
            writeElement(tag, attributes);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleEndTag(HTML.Tag tag, int position) {
        try {
            out.writeEndElement();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void handleText(char[] data, int pos) {
        try {
            out.writeCharacters(data, 0, data.length);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void handleComment(char[] data, int pos) {
        try {
            out.writeComment(new String(data));
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}