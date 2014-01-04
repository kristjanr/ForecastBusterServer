package forecastbuster.incoming;

import com.google.appengine.api.urlfetch.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class XMLDownloadAndParse {
    static Logger log = Logger.getLogger(XMLDownloadAndParse.class.getName());

    public XMLDownloadAndParse() {
    }

    public static Document getDocFromUrl(String urlString) {
        String xml = "";
        StringBuilder stringBuilder = new StringBuilder();

        log.info("Starting to fetch data from url: " + urlString);

        URL url = null;
        HTTPResponse response = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert url != null;
        URLFetchService urlFetchService = URLFetchServiceFactory.getURLFetchService();
        HTTPRequest httpRequest = new HTTPRequest(url, HTTPMethod.GET, FetchOptions.Builder.withDeadline(60.0));
        try {
            response = urlFetchService.fetch(httpRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response == null) {
            return null;
        }
        try {
            xml = new String(response.getContent(), "iso-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Document doc = getDomElement(xml);
        log.info("Finished fetching data.");
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

        } catch (Exception e) {
            log.severe("Error while building xml document from xml string" + e);
            return null;
        }
        return doc;
    }
}
