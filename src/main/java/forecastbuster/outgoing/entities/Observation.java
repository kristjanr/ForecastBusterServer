package forecastbuster.outgoing.entities;

import java.util.Calendar;
import java.util.List;

public class Observation {
    Calendar date;
    List<Station> stations;

    public Observation(Calendar date, List<Station> stations) {
        this.date = date;
        this.stations = stations;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }
}
