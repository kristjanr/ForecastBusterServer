package forecastbuster.outgoing.entities;

import java.util.Calendar;

public class ForecastedDay {
    private Calendar dateOfForecast;
    private Forecast forecasted1DayBefore;
    private Forecast forecasted2DaysBefore;
    private Forecast forecasted3DaysBefore;
    private Forecast forecasted4DaysBefore;

    public ForecastedDay(Calendar currentDate) {
        setDateOfForecast(currentDate);
    }

    public void setDateOfForecast(Calendar dateOfForecast) {
        this.dateOfForecast = dateOfForecast;
    }

    public Calendar getDateOfForecast() {
        return dateOfForecast;
    }

    public Forecast getForecasted1DayBefore() {
        return forecasted1DayBefore;
    }

    public void setForecasted1DayBefore(Forecast forecasted1DayBefore) {
        this.forecasted1DayBefore = forecasted1DayBefore;
    }

    public Forecast getForecasted2DaysBefore() {
        return forecasted2DaysBefore;
    }

    public void setForecasted2DaysBefore(Forecast forecasted2DaysBefore) {
        this.forecasted2DaysBefore = forecasted2DaysBefore;
    }

    public Forecast getForecasted3DaysBefore() {
        return forecasted3DaysBefore;
    }

    public void setForecasted3DaysBefore(Forecast forecasted3DaysBefore) {
        this.forecasted3DaysBefore = forecasted3DaysBefore;
    }

    public Forecast getForecasted4DaysBefore() {
        return forecasted4DaysBefore;
    }

    public void setForecasted4DaysBefore(Forecast forecasted4DaysBefore) {
        this.forecasted4DaysBefore = forecasted4DaysBefore;
    }

    public void setForecastedDay(int daysBefore, Forecast forecast) {
        switch (daysBefore) {
            case 0:
                setForecasted1DayBefore(forecast);
                break;
            case 1:
                setForecasted2DaysBefore(forecast);
                break;
            case 2:
                setForecasted3DaysBefore(forecast);
                break;
            case 3:
                setForecasted4DaysBefore(forecast);
                break;
        }
    }

    public boolean isEmpty() {
        return getForecasted1DayBefore() == null && getForecasted2DaysBefore() == null && getForecasted3DaysBefore() == null && getForecasted4DaysBefore() == null;
    }
}
