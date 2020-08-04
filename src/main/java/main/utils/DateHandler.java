package main.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateHandler {

  public static Calendar getDateFromString(String dateString)
  {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    try {
      calendar.setTime(format.parse(dateString));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    return calendar;
  }

}
