package ee.kristjan;

public class Place {
    private long id;
    private String name;
    private String phenomenon;
    private float tempMin;
    private float tempMax;

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    private Forecast forecast;

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

    public float getTempMin() {
        return tempMin;
    }

    public void setTempMin(float tempMin) {
        this.tempMin = tempMin;
    }

    public float getTempMax() {
        return tempMax;
    }

    public void setTempMax(float tempMax) {
        this.tempMax = tempMax;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
