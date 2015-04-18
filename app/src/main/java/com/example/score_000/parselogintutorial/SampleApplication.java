package com.example.score_000.parselogintutorial;

/**
 * Created by score_000 on 2/12/2015.
 */

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

public class SampleApplication extends Application {

    public void onCreate(){
        ParseObject.registerSubclass(Report.class);
        ParseObject.registerSubclass(Car.class);
        Parse.initialize(this, "p7qbQVIpRF5OmglZEgdgbrQ2XOlN9Gl4wk2Rd9X7", "7r9TDOHAd22nu7luqf02SXP0KyNoiOO8sn2djMtD");
        ParseInstallation.getCurrentInstallation().saveInBackground();
/*        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });*/
        PushService.setDefaultPushCallback(this, MainActivity.class);
    }
}