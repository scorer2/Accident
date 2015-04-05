package com.example.score_000.parselogintutorial;

/**
 * Created by score_000 on 2/12/2015.
 */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class DispatchActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // Check if there is current user info

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseUser currUser = (ParseUser) object;
                        boolean emailVerified = currUser.getBoolean("emailVerified");
                        if (emailVerified){
                            // Email is verified so Start an intent for the logged in activity
                            startActivity(new Intent(DispatchActivity.this, MainActivity.class));
                        }
                        else{
                            final AlertDialog.Builder builder = new AlertDialog.Builder(DispatchActivity.this);
                            builder.setTitle("Please verify your email!");
                            // set message
                            builder.setCancelable(false);
                            builder.setPositiveButton("Continue",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            final ProgressDialog dlg = new ProgressDialog(DispatchActivity.this);
                                            dlg.setTitle("Please wait...");
                                            dlg.setMessage("Verifying...");
                                            dlg.show();
                                            startActivity(new Intent(DispatchActivity.this, DispatchActivity.class));//
                                        }
                                    });

                            builder.setNeutralButton("Cancel",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which){
                                            ParseUser.getCurrentUser().logOut();
                                            startActivity(new Intent(DispatchActivity.this, DispatchActivity.class));
                                        }
                                    });

                            builder.setNegativeButton("Resend",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                            ParseUser user = ParseUser.getCurrentUser();
                                            String email = user.getEmail();
                                            String altEmail = null;
                                            if (email.substring(0, 1).matches("[a-z]+")) {
                                                altEmail = email.substring(0).toUpperCase();
                                            } else {
                                                altEmail = email.substring(0).toLowerCase();
                                            }
                                            user.setEmail(altEmail);//user.getEmail());
                                            user.saveInBackground();
                                            Toast.makeText(DispatchActivity.this, "Email Sent.", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(DispatchActivity.this, DispatchActivity.class));
                                        }
                                    });

                            AlertDialog alert = builder.create();

                            // Show Alert Dialog
                            alert.show();
                        }
                    }
                    else {
                        // Failure!
                    }
                }
            });
        }
        else {
            // Start and intent for the logged out activity
            startActivity(new Intent(this, SignUpOrLoginActivity.class));
        }
    }
}