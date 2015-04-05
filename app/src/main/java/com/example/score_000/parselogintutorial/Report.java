package com.example.score_000.parselogintutorial;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by score_000 on 3/29/2015.
 */

@ParseClassName("Report")
public class Report extends ParseObject {

    private String title;
    private String driverName;

    /*public Report(String title, String driverName){
        this.title = title;
        this.driverName = driverName;
    }*/

    public Report() {

    }

    public String getID(){return getString("UserID");}

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("Location");
    }

    public String getDriverName() {
        return getString("DriverName");
    }

    public String getUserName(){return getString("User");}

    public Date getTime(){return getCreatedAt();}

    public String getTitle(){return getString("Title");}

    public String getCount(){return getString("Count");}

    public void setCount(String value) {put("Count", value);}

    public void setID(Object value){put("UserID", value);}

    public void setLocation(ParseGeoPoint value) {
        put("Location", value);
    }

    public void setDriverName(String value) {
        put("DriverName", value);
    }

    public void setUserName(String value) {put("User", value);}

    public void setTitle(String value) {put("Title", value);}

    public static ParseQuery<Report> getQuery() {
        return ParseQuery.getQuery(Report.class);
    }
}