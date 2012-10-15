import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Station {


    private long id;
    private String name;
    private String phenomenon;
    private Float airTemperature;
    private Observation observation;
    private static final String KEY_AIRTEMPERATURE = "airtemperature";

    public Station(Observation observation, NodeList nodeList) {
        this.observation = observation;
        for(int i = 0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            String nodeName = node.getNodeName();
            if(nodeName.equals(Main.KEY_NAME)) {
                setName(node.getTextContent());
            } else if (nodeName.equals(Main.KEY_PHENOMENON) && !node.getTextContent().isEmpty()){
                setPhenomenon(node.getTextContent());
            } else if (nodeName.equals(KEY_AIRTEMPERATURE) && !node.getTextContent().isEmpty()){
                setAirTemperature(node.getTextContent());
            }
        }
    }

    public void createStation(NodeList thirdNodeList) {
    }

    public void setAirTemperature(String temp) {
        setAirTemperature(Float.parseFloat(temp));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public Float getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(Float airTemperature) {
        this.airTemperature = airTemperature;
    }

    public Observation getObservation() {
        return observation;
    }

    public void setObservation(Observation observation) {
        this.observation = observation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
