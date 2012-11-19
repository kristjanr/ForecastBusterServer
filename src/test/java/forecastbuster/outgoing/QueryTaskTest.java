package forecastbuster.outgoing;

import forecastbuster.outgoing.entities.Forecast;
import forecastbuster.outgoing.entities.ForecastedDay;
import forecastbuster.outgoing.entities.Place;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class QueryTaskTest {
    Query mockedQuery;
    QueryTask queryTask;

    @Before
    public void setUp() {
        mockedQuery = mock(Query.class);
//        doReturn(null).when(mockedQuery).getDatabaseAccessObject();
        queryTask = spy(new QueryTask(mockedQuery));
    }

    @Test
    public void createForecastsShouldReturnTreeMapOfForecastsForEachEntryInGivenList() {
        Date date1 = new Date(Date.valueOf("2012-11-17").getTime());
        Date date2 = new Date(Date.valueOf("2012-11-18").getTime());
        Object[] forecastDataFromQuery1 = {date1, "", 0.0, 0.0, "", 0.0, 0.0};
        Object[] forecastDataFromQuery2 = {date2, "", 0.0, 0.0, "", 0.0, 0.0};
        List queryList = new ArrayList();
        queryList.add(forecastDataFromQuery1);
        queryList.add(forecastDataFromQuery2);

        TreeMap<Calendar, Forecast> forecastTreeMap = queryTask.createForecasts(queryList);

        assertEquals(2, forecastTreeMap.size());
        assertEquals(date1, forecastTreeMap.firstKey().getTime());
        assertEquals(date2, forecastTreeMap.lastKey().getTime());
    }

    @Test
    public void doQueryAndPutForecastMapsToList() {
        Date date1 = new Date(Date.valueOf("2011-12-17").getTime());
        Date date2 = new Date(Date.valueOf("2012-11-01").getTime());
        Object[] forecastDataFromQuery1 = {date1, "", 0.0, 0.0, "", 0.0, 0.0
        };
        Object[] forecastDataFromQuery2 = {date2, "", 0.0, 0.0, "", 0.0, 0.0};
        List queryList1 = new ArrayList();
        queryList1.add(forecastDataFromQuery1);
        List queryList2 = new ArrayList();
        queryList2.add(forecastDataFromQuery1);
        queryList2.add(forecastDataFromQuery2);
        List queryList3 = new ArrayList();
        queryList3.add(forecastDataFromQuery1);
        doReturn(queryList1).when(queryTask).queryForecastsForGivenDayAfterToday(0);
        doReturn(queryList2).when(queryTask).queryForecastsForGivenDayAfterToday(1);
        doReturn(queryList3).when(queryTask).queryForecastsForGivenDayAfterToday(2);
        doReturn(new ArrayList()).when(queryTask).queryForecastsForGivenDayAfterToday(3);

        queryTask.doQueryAndPutForecastMapsToList();

        assertEquals(date1, queryTask.earliestDate.getTime());
        assertEquals(3, queryTask.fourDayForecastQueries.size());
        for (int i = 0; i < queryTask.fourDayForecastQueries.size(); i++) {
            TreeMap<Calendar, Forecast> treeMap = (TreeMap<Calendar, Forecast>) queryTask.fourDayForecastQueries.get(i);
            assertEquals(date1, treeMap.firstKey().getTime());
            if (i == 1) {
                assertEquals(date2, treeMap.lastKey().getTime());
            }
        }
    }

    @Test
    public void createMapOfPlacesListsShouldCreateMapWithPlacesListsAsValuesAndDatesAsKeys() {

        Date date1 = new Date(Date.valueOf("2012-11-17").getTime());
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Date date2 = new Date(Date.valueOf("2012-11-19").getTime());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        String name1 = "Jõhvi";
        String name2 = "Harku";
        String nightphenomenon = "";
        double nighttempmin = 0.0;
        String dayphenomenon = "";
        double daytempmax = 0.0;
        Object[] placeData1 = {date1, name1, dayphenomenon, daytempmax, nightphenomenon, nighttempmin};
        Object[] placeData2 = {date2, name2, dayphenomenon, daytempmax, nightphenomenon, nighttempmin};
        List places = new ArrayList<Place>();
        places.add(placeData1);
        places.add(placeData2);
        queryTask.earliestDate = cal1;
        queryTask.latestDate = cal2;
        doReturn(places).when(queryTask).getPlacesForecastsForTomorrow();

        TreeMap<Calendar, ArrayList<Place>> placeMap = queryTask.createMapOfPlaceLists();

        assertEquals(date1, placeMap.firstKey().getTime());
        assertEquals(date2, placeMap.lastKey().getTime());
        ArrayList placeList = placeMap.firstEntry().getValue();
        assertEquals(name1, ((Place) placeList.get(0)).getName());
        placeList = (ArrayList) placeMap.lastEntry().getValue();
        assertEquals(name2, ((Place) placeList.get(0)).getName());
    }

    @Test
    public void createForecastedDaysShould() {
        Date date3 = new Date(Date.valueOf("2012-11-17").getTime());
        Date date4 = new Date(Date.valueOf("2012-11-19").getTime());
        Object[] forecastDataFromQuery1 = {date3, "", 0.0, 0.0, "", 0.0, 0.0 };
        String nightPhen = "phenomenon of forecast with Harku place";
        Object[] forecastDataFromQuery2 = {date4, nightPhen, 0.0, 0.0, "", 0.0, 0.0};
        List queryList1 = new ArrayList();
        queryList1.add(forecastDataFromQuery1);
        queryList1.add(forecastDataFromQuery2);
        List queryList2 = new ArrayList();
        queryList2.add(forecastDataFromQuery1);
        List queryList3 = new ArrayList();
        queryList3.add(forecastDataFromQuery1);
        doReturn(queryList1).when(queryTask).queryForecastsForGivenDayAfterToday(0);
        doReturn(queryList2).when(queryTask).queryForecastsForGivenDayAfterToday(1);
        doReturn(queryList3).when(queryTask).queryForecastsForGivenDayAfterToday(2);
        doReturn(new ArrayList()).when(queryTask).queryForecastsForGivenDayAfterToday(3);
        queryTask.doQueryAndPutForecastMapsToList();
        Date date1 = new Date(Date.valueOf("2012-11-17").getTime());
        Date date2 = new Date(Date.valueOf("2012-11-19").getTime());
        String name1 = "Jõhvi";
        String name2 = "Harku";
        String nightphenomenon = "";
        double nighttempmin = 0.0;
        String dayphenomenon = "";
        double daytempmax = 0.0;
        Object[] placeData1 = {date1, name1, dayphenomenon, daytempmax, nightphenomenon, nighttempmin};
        Object[] placeData2 = {date2, name2, dayphenomenon, daytempmax, nightphenomenon, nighttempmin};
        List places = new ArrayList<Place>();
        places.add(placeData1);
        places.add(placeData2);
        doReturn(places).when(queryTask).getPlacesForecastsForTomorrow();
        TreeMap<Calendar, ArrayList<Place>> placeMap = queryTask.createMapOfPlaceLists();

        TreeMap<Calendar, ForecastedDay> forecastedDayTreeMap = queryTask.createForecastedDays(placeMap);

        assertEquals(2,forecastedDayTreeMap.size());
        assertEquals(date3, forecastedDayTreeMap.firstKey().getTime());
        assertEquals(date4,forecastedDayTreeMap.lastKey().getTime());
        ForecastedDay forecastedDay = forecastedDayTreeMap.lastEntry().getValue();
        assertEquals(nightPhen,forecastedDay.getForecasted1DayBefore().getNightPhenomenon());
        assertEquals(name2,forecastedDay.getForecasted1DayBefore().getPlaces().get(0).getName());
    }
}
