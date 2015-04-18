package com.example.score_000.parselogintutorial;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by score_000 on 4/14/2015.
 */
@ParseClassName("Car")
public class Car extends ParseObject {

    public Car() {
        //default constructor
    }

    public String getID(){return getString("UserID");}

    public String getMake() {
        return getString("Make");
    }

    public String getModel(){return getString("Model");}

    public int getYear() {return getInt("Year");}

    public Date getTime(){return getCreatedAt();}

    public String getColor(){return getString("Color");}

    public String getLicensePlate(){return getString("License_Plate");}

    public String getVIN(){return getString("VIN");}

    public void setID(Object value) {put("UserID", value);}

    public void setMake(String value) {put("Make", value);}

    public void setModel(String value) {put("Model", value);}

    public void setYear(int value) {put("Year", value);}

    public void setColor(String value) {put("Color", value);}

    public void setLicensePlate(String value) {put("License_Plate", value);}

    public void setVIN(String value) {put("VIN", value);}

    public static ParseQuery<Car> getQuery() {
        return ParseQuery.getQuery(Car.class);
    }
}
