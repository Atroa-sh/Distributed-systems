package com.example.zad2_rest;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;

import java.io.*;


public class CreateResponse {
    public String getHtmlResponse2(int day, int month, int year, int nrOfEvents) throws IOException, JSONException {
        EventsFetcher eventsFetcher = new EventsFetcher(day, month, nrOfEvents);
        HolidaysFetcher holidaysFetcher = new HolidaysFetcher(day, month, year);
        eventsFetcher.start();
        holidaysFetcher.start();
        try {
            eventsFetcher.join();
            holidaysFetcher.join();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        String pageName = eventsFetcher.getPageName();
        String events = eventsFetcher.getEvents();
        String holidays = holidaysFetcher.getHolidays();
        if (holidays != null)holidays += "<br><a href=\"../index.html\">click to return</a>";
        File htmlTemplateFile = new File("C:\\Users\\kacpe\\IdeaProjects\\rozprochy\\Zad2_REST\\template.html");
        String htmlString = FileUtils.readFileToString(htmlTemplateFile);
        try{
            htmlString = htmlString.replace("$title", pageName);
            htmlString = htmlString.replace("$events", events);
        }
        catch (NullPointerException e){
            htmlString = htmlString.replace("$title", "");
            htmlString = htmlString.replace("$events", "An error occurred while fetching. Maybe a form wasn't filled correctly<br>");
        }
        try {
            htmlString = htmlString.replace("$holidays", holidays);
        }
        catch (NullPointerException e){
            htmlString = htmlString.replace("$holidays", "no available data for holidays in year" + year + "<br><a href=\"../index.html\">click to return</a>");
        }

        return htmlString;
    }
}
