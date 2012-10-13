import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Hello world!
 */
public class Main {
    static Logger log = LoggerFactory.getLogger(XMLParser.class);
    private static DatabaseAccessObject databaseAccessObject = null;
    private static final String URL = "http://www.emhi.ee/ilma_andmed/xml/forecast.php";
    static final String KEY_FORECAST = "forecast"; // grandparent node
    static final String KEY_DATE = "date";
    static final String KEY_NIGHT = "night"; // parent node
    static final String KEY_DAY = "day"; // parent node
    static final String KEY_PHENOMENON = "phenomenon";
    static final String KEY_TEMPMIN = "tempmin";
    static final String KEY_TEMPMAX = "tempmax";
    static final String KEY_TEXT = "text";
    static final String KEY_PLACE = "place"; // parent node
    static final String KEY_NAME = "name";
    static final String KEY_WIND = "wind"; // parent node
    static final String KEY_DIRECTION = "direction";
    static final String KEY_SPEEDMIN = "speedmin";
    static final String KEY_SPEEDMAX = "speedmax";
    static final String KEY_SEA = "sea";
    static final String KEY_PEIPSI = "peipsi";


    public static void main(String[] args) {
        getDatabaseAccessObject().initSession();
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(URL); // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_FORECAST);
        ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
        // looping through all item nodes <item>
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            HashMap<String, String> map = new HashMap<String, String>();
            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
            // lisada ka �� ja p�ev
            map.put(KEY_DATE, e.getAttribute(KEY_DATE));
            map.put(KEY_PHENOMENON, parser.getValue(e, KEY_PHENOMENON));
            map.put(KEY_TEMPMAX, parser.getValue(e, KEY_TEMPMAX));
            map.put(KEY_TEMPMIN, parser.getValue(e, KEY_TEMPMIN));
            map.put(KEY_TEXT, parser.getValue(e, KEY_TEXT));

            // adding HashList to ArrayList
            menuItems.add(map);
            log.debug(map.toString());
        }

    }

    public static DatabaseAccessObject getDatabaseAccessObject() {
        if (databaseAccessObject == null) {
            databaseAccessObject = new DatabaseAccessObject();
        }
        return databaseAccessObject;
    }

    public static void setDatabaseAccessObject(DatabaseAccessObject databaseAccessObject) {
        Main.databaseAccessObject = databaseAccessObject;
    }
}
