import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class XMLParser {
    Logger log = LoggerFactory.getLogger(XMLParser.class);

    // constructor
    public XMLParser() {

    }


    public static Document getDocFromUrl(String url) {
        String xml = null;

        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Document doc = getDomElement(xml);
        return doc;
    }

    public static Document getDocFromFile(String location) {
        Document doc = null;
        try {
            File fXmlFile = new File(location);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
        e.printStackTrace();
        }
        return doc;
    }
    public Document getDomElement(String xml) {
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = documentBuilder.parse(is);

        } catch (ParserConfigurationException e) {
            log.error("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            log.error("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            log.error("Error: ", e.getMessage());
            return null;
        }
        return doc;
    }

    public final String getElementValue(Node elem) {
        Node child;
        if (elem != null) {
            if (elem.hasChildNodes()) {
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    if (child.getNodeType() == Node.TEXT_NODE) {
                        return child.getNodeValue();
                    } else if (child.getNodeName() == Main.KEY_NIGHT) {
                        return Main.KEY_NIGHT;
                    } else if (child.getNodeName() == Main.KEY_DAY) {
                        return Main.KEY_DAY;
                    }
                }
            }
        }
        return "";
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return getElementValue(n.item(0));
    }
}
