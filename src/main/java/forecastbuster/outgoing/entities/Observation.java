package forecastbuster.outgoing.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Observation {
    private Calendar date;
    private String dateString;
    private List<Station> stations;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            cal.setTime(sdf.parse(dateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        setDate(cal);
    }

    public Observation(Calendar date, List<Station> stations) {
        setDate(date);
        setStations(stations);
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String dateString = sdf.format(date.getTime());
        this.dateString = dateString;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }
}
