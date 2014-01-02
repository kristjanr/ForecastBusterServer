package forecastbuster.incoming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

public class XMLFetcher {
    static Logger log = LoggerFactory.getLogger(XMLFetcher.class);

    public XMLFetcher() {
    }

    public static Document getDocFromUrl(String urlString) {
        String xml;
        StringBuilder stringBuilder = new StringBuilder();

        log.debug("Starting to fetch data from url: " + urlString);
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();

        } catch (MalformedURLException e) {
            log.error("Bad url", e);

        } catch (IOException e) {
            log.error("IO Error", e);

        }
        xml = stringBuilder.toString();
        Document doc = getDomElement(xml);
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

        } catch (Exception e) {
            log.error("Error while building xml document from xml string", e);
            return null;
        }
        return doc;
    }
}
