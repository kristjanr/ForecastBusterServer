import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class XMLFetcher {
    static Logger log = LoggerFactory.getLogger(XMLFetcher.class);

    public XMLFetcher() {
    }

    public static Document getDocFromUrl(String url) {
        String xml = null;
        log.debug("Starting to fetch data from url: " + url);
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        Document doc = getDomElement(xml);
        log.debug("Finished fetching data.");
        return doc;
    }

    public static Document getDocFromFile(String location) {
        Document doc = null;
        log.debug("Starting to fetch data from file: " + location);
        try {
            File fXmlFile = new File(location);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug("Finished fetching data.");
        return doc;
    }

    public static Document getDomElement(String xml) {
        Document doc;
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
}
