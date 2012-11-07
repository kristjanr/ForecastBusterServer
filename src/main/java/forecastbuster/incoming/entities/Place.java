package forecastbuster.incoming.entities;

import forecastbuster.Main;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Place {
    private long id;
    private String name;
    private String phenomenon;
    private Float tempMin = null;
    private Float tempMax = null;
    private Forecast forecast;

    static void createPlace(Forecast forecast, Node thirdNode) {
        Place place = new Place();
        NodeList fourthNodeList = thirdNode.getChildNodes();

        for (int m = 0; m < fourthNodeList.getLength(); m++) {
            Node fourthNode = fourthNodeList.item(m);
            String nodeName = fourthNode.getNodeName();

            if (nodeName.equals(Main.KEY_PHENOMENON)) {
                place.setPhenomenon(fourthNode.getTextContent());
            } else if (nodeName.equals(Main.KEY_TEMPMAX)) {
                place.setTempMax(fourthNode.getTextContent());
            } else if (nodeName.equals(Main.KEY_TEMPMIN)) {
                place.setTempMin(fourthNode.getTextContent());
            } else if (nodeName.equals(Main.KEY_NAME)) {
                place.setName(fourthNode.getTextContent());
            }
        }
        place.setForecast(forecast);
        forecast.getPlaces().add(place);
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Name: ");
        stringBuffer.append(getName());
        stringBuffer.append(" Phenomenon: ");
        stringBuffer.append(getPhenomenon());
        stringBuffer.append(" MinTemp: ");
        stringBuffer.append(getTempMin());
        stringBuffer.append(" MaxTemp: ");
        stringBuffer.append(getTempMax());
        stringBuffer.append("\n");
        return stringBuffer.toString();
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
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

    public Float getTempMin() {
        return tempMin;
    }

    public void setTempMin(Float tempMin) {
        this.tempMin = tempMin;
    }

    public Float getTempMax() {
        return tempMax;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = Float.parseFloat(tempMin);
    }

    public void setTempMax(String tempMax) {
        this.tempMax = Float.parseFloat(tempMax);
    }

    public void setTempMax(Float tempMax) {
        this.tempMax = tempMax;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
