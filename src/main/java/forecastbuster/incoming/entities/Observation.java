package forecastbuster.incoming.entities;

import forecastbuster.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Observation {


    private long id;
    private Date timestamp;
    private List<Station> stations = new ArrayList<>(39);

    public void createObservation(NodeList nodeList) throws ParseException {
        Element element = (Element) nodeList.item(0);
        String timestamp = element.getAttribute(Constants.TIMESTAMP);
        setTimestamp(timestamp);
        NodeList secondNodeList = element.getChildNodes();
        for (int i = 0; i < secondNodeList.getLength(); i++) {
            Node secondNode = secondNodeList.item(i);
            if (secondNode.getNodeName() == Constants.STATION) {
                NodeList thirdNodeList = secondNode.getChildNodes();
                Station station = new Station(this, thirdNodeList);
                stations.add(station);
            }
        }
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public void setTimestamp(String date) throws ParseException {
        setTimestamp(new SimpleDateFormat("s").parse(date));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }
}

