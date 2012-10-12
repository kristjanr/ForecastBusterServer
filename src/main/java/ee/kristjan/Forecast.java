package ee.kristjan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Forecast {
    private long id;
    private Date date;
    private String timeOfDay;
    private Date timeOfUpdate = new Date();
    private String phenomenon;
    private String text;
    private List<Place> places = new ArrayList<Place>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
