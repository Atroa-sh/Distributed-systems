package com.example.zad2_rest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Random;

public class EventsFetcher extends Thread{
    private final int day;
    private final int month;
    private final int nrOfEvents;
    private String events;
    private String pageName;

    public EventsFetcher(int day, int month, int nrOfEvents) {
        this.day = day;
        this.month = month;
        this.nrOfEvents = nrOfEvents;
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }


    private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }


    public void run(){
        try {
            this.events = getWikipediaEvents(day, month, nrOfEvents);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private String getWikipediaEvents(int day, int month, int nrOfEvents) throws IOException{
        Random RNG = new Random();
        StringBuilder eventsBuilder = new StringBuilder();
        String paragraphTemplate = "<p><font size=\"+2\">%s</font></p>\n" +
                "%s";
        StringBuilder paragraphBuilder = new StringBuilder();
        String url = String.format("https://byabbe.se/on-this-day/%d/%d/events.json", month, day);
        JSONObject data = readJsonFromUrl(url);
        String pageName = data.getString("date");
        this.pageName = pageName;
        eventsBuilder.append(String.format("<p><font size=\"+2\">%s</font></p>\n", pageName));
        eventsBuilder.append("<p><font size=\"+1\">Following events happened on this day: </font></p>");
        JSONArray events = data.getJSONArray("events");
        if(nrOfEvents < 1) nrOfEvents = 0;
        else if (nrOfEvents > events.length()) nrOfEvents = events.length();
        int index;
        for(int i = nrOfEvents; i>0;i--){
            paragraphBuilder.setLength(0); //clears StringBuilder
            index = RNG.nextInt(events.length());
            JSONObject event = events.getJSONObject(index);
            String year = event.getString("year");
            paragraphBuilder.append(event.getString("description")).append("\n").append("<br>").append("links: \n").append("<br>");
            JSONArray links = event.getJSONArray("wikipedia");
            for(int j = 0 ; j < links.length(); j++){
                JSONObject link = links.getJSONObject(j);
                paragraphBuilder.append(link.getString("title")).append(": ").append(String.format("<a href=\"%s\">link</a>", link.getString("wikipedia"))).append("\n").append("<br>");
            }
            paragraphBuilder.append("\n").append("<br>");
            eventsBuilder.append(String.format(paragraphTemplate, year, paragraphBuilder.toString()));
        }
        return eventsBuilder.toString();
    }


    public String getEvents() {
        return events;
    }

    public String getPageName() {
        return pageName;
    }
}
