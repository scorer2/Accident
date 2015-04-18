package com.example.score_000.parselogintutorial;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by score_000 on 3/29/2015.
 */

@ParseClassName("Report")
public class Report extends ParseObject {

    public Report() {
        //default constructor
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

    public Boolean getTowing(){return getBoolean("Towing");}

    public ParseFile getPhoto() {return getParseFile("Photo");}

    public Boolean getHidden() {return getBoolean("Hidden");}

    public long getPhoneNumber() {return getLong("PhoneNumber");}

    public String getDescription() {return getString("Description");}

    public void setTowing(Boolean value) {put("Towing", value);}

    public void setID(Object value){put("UserID", value);}

    public void setLocation(ParseGeoPoint value) {
        put("Location", value);
    }

    public void setDriverName(String value) {
        put("DriverName", value);
    }

    public void setUserName(String value) {put("User", value);}

    public void setTitle(String value) {put("Title", value);}

    public void setTime(Date value) {put("Time", value);}

    public void setPhoto (ParseFile value) {put("Photo", value);}

    public void setHidden (Boolean value) {put("Hidden", value);}

    public void setPhoneNumber (long value) {put("PhoneNumber", value);}

    public void setDescription (String value) {put("Description", value);}

    public static ParseQuery<Report> getQuery() {
        return ParseQuery.getQuery(Report.class);
    }
}