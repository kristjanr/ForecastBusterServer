package forecastbuster.outgoing;


import java.util.Calendar;
import java.util.Comparator;

class MyDateComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        Calendar date1 = (Calendar) o1;
        Calendar date2 = (Calendar) o2;

        return date1.compareTo(date2);
    }
}

