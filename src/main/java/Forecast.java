import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Forecast {
    private long id;
    private Date date;
    private String timeOfDay;
    private Date timeOfUpdate = new Date();
    private String phenomenon;
    private float tempMin;
    private float tempMax;
    private String text;
    private List<Place> places = new ArrayList<Place>();

    void createForecast(Element firstElement, Node secondNode) throws ParseException {
        if (secondNode.getNodeName() == Main.KEY_NIGHT || secondNode.getNodeName() == Main.KEY_DAY) {

            setDate(firstElement.getAttribute(Main.KEY_DATE));

            setTimeOfDay(secondNode.getNodeName());
            NodeList thirdNodeList = secondNode.getChildNodes();

            for (int k = 0; k < thirdNodeList.getLength(); k++) {
                Node thirdNode = thirdNodeList.item(k);
                String s = thirdNode.getNodeName();

                if (s.equals(Main.KEY_PHENOMENON)) {
                    setPhenomenon(thirdNode.getFirstChild().getTextContent());
                } else if (s.equals(Main.KEY_TEMPMAX)) {
                    setTempMax(thirdNode.getFirstChild().getTextContent());

                } else if (s.equals(Main.KEY_TEMPMIN)) {
                    setTempMin(thirdNode.getFirstChild().getTextContent());

                } else if (s.equals(Main.KEY_TEXT)) {
                    setText(thirdNode.getFirstChild().getTextContent());
                } else if (s.equals(Main.KEY_PLACE)) {

                    Place.createPlace(this, thirdNode);
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Date: ");
        stringBuffer.append(getDate());
        stringBuffer.append(" timeOfDay: ");
        stringBuffer.append(getTimeOfDay());
        stringBuffer.append(" Phenomenon: ");
        stringBuffer.append(getPhenomenon());
        stringBuffer.append(" Text: ");
        stringBuffer.append(getText());
        stringBuffer.append(" MinTemp: ");
        stringBuffer.append(getTempMin());
        stringBuffer.append(" MaxTemp: ");
        stringBuffer.append(getTempMax());
        stringBuffer.append("\n");
        stringBuffer.append(" Places");
        for (int l = 0; l < places.size(); l++) {
            stringBuffer.append(places.get(l));
        }
        return stringBuffer.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDate(String date) throws ParseException {
        this.date = new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public Date getTimeOfUpdate() {
        return timeOfUpdate;
    }

    public void setTimeOfUpdate(Date timeOfUpdate) {
        this.timeOfUpdate = timeOfUpdate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhenomenon() {
        return phenomenon;
    }

    public void setPhenomenon(String phenomenon) {
        this.phenomenon = phenomenon;
    }

    public float getTempMin() {
        return tempMin;
    }

    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = Float.parseFloat(tempMin);
    }

    public float getTempMax() {
        return tempMax;
    }

    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = Float.parseFloat(tempMax);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }
}
