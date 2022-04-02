package com.example.zad2_rest;


import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

@Path("/days")
public class DaysResource {
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String  daysHandler(@FormParam("day") int day,
                            @FormParam("month") int month,
                            @FormParam("year") int year,
                            @FormParam("nr") int nrOfEvents,
                            @Context HttpServletResponse servletResponse) throws IOException {
        CreateResponse response = new CreateResponse();
        System.out.println(day + " " + month + " " + year + " " + nrOfEvents);
        String htmlResponse = "";
        try {
            htmlResponse = response.getHtmlResponse2(day, month, year, nrOfEvents);
        }
        catch (Exception e){
            e.printStackTrace();
            servletResponse.sendRedirect("../error.html");
        }

        return htmlResponse;
    }
}
