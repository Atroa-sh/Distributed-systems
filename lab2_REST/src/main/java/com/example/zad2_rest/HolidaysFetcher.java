package com.example.zad2_rest;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class HolidaysFetcher extends Thread{
    private final String key = "506c95fc62364304a806ca50e88019cf";
    private final int day;
    private final int month;
    private final int year;
    private String holidays;

    public HolidaysFetcher(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public void run(){
        try {
            holidays = getHolidays(day, month, year);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private String getHolidays(int day, int month, int year) throws IOException {
        StringBuilder holidayBuilder = new StringBuilder();
        String url = String.format("https://holidays.abstractapi.com/v1/?api_key=%s&country=PL&year=%d&month=%d&day=%d", key, year, month, day);
        Content content = Request.Get(url)
                .execute().returnContent();
        JSONArray array = new JSONArray(content.toString());
        holidayBuilder.append(String.format("<p><font size=\"+1\">In Poland following holidays take place on this day in %d:</font></p>", year));
        for(int i = 0 ; i < array.length() ; i++){
            JSONObject event = array.getJSONObject(i);
            holidayBuilder.append(event.getString("name")).append("<br>");
        }
        return holidayBuilder.toString();
    }

    public String getHolidays() {
        return holidays;
    }
}
