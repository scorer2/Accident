package com.example.score_000.parselogintutorial;

/**
 * Created by score_000 on 2/12/2015.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class SampleApplication extends Application {

    public void onCreate(){
        ParseObject.registerSubclass(Report.class);
        Parse.initialize(this, "p7qbQVIpRF5OmglZEgdgbrQ2XOlN9Gl4wk2Rd9X7", "7r9TDOHAd22nu7luqf02SXP0KyNoiOO8sn2djMtD");
    }
}