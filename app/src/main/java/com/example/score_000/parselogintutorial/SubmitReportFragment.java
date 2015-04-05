package com.example.score_000.parselogintutorial;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;

public class SubmitReportFragment extends Fragment {

    public ParseGeoPoint geoPoint;
    private GPSLocation locationGPS;
    private MapActivity locationMap;
    private String id;
    String dName;
    String dLastName;
    String title;
    boolean validationError = false;

    public SubmitReportFragment() {
    }

    ParseUser currentUser = ParseUser.getCurrentUser();// Retrieve current user from Parse.com
    ArrayList<String> questions = (ArrayList<String>) currentUser.get("Questions");
    String typeOfPlan = currentUser.getString("PlanType");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_submit_report, container, false);
        final String planTypeArray[] = getResources().getStringArray(R.array.insuranceArray);
        final RadioButton rdbtnYes = (RadioButton) rootView.findViewById(R.id.rdbtnYes);
        final RadioButton rdbtnNo = (RadioButton) rootView.findViewById(R.id.rdbtnNo);
        final RadioButton rdbtnUseGPS = (RadioButton) rootView.findViewById(R.id.rdbtnUseGPS);
        final EditText txtName = (EditText) rootView.findViewById(R.id.txtName);
        final EditText txtLastName = (EditText) rootView.findViewById(R.id.txtLastName);

        if (questions != null) {
            if (questions.size() < 2) {
                goBackToQuestions();
            }
            else {
                //submit a report
                rdbtnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rdbtnYes.setChecked(false);
                        if (typeOfPlan.equals(planTypeArray[1])) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("You need to upgrade your plan to at least '" + planTypeArray[2] + "'.");
                            // set message
                            builder.setCancelable(false);
                            builder.setPositiveButton("Take me there now",
                                    new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            Intent intent = new Intent(getActivity(), MainActivity.class);
                                            getActivity().startActivity(intent);
                                        }
                                    });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // TODO Auto-generated method stub
                                    // close the dialog box
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alert = builder.create();
                            // Show Alert Dialog
                            alert.show();
                        }
                        else {
                            //submit report
                            txtName.setVisibility(View.VISIBLE);
                            txtLastName.setVisibility(View.VISIBLE);
                        }
                    }
                });
                rdbtnYes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        rdbtnNo.setChecked(false);
                        txtName.setVisibility(View.GONE);
                        txtLastName.setVisibility(View.GONE);
                    }
                });
            }
        }
        else {
            goBackToQuestions();
        }

        final Button btnLaunchMap = (Button) rootView.findViewById(R.id.btnLaunchMap);
        btnLaunchMap.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                rdbtnUseGPS.setChecked(false);
                startActivity(new Intent(getActivity(), MapActivity.class));
            }
        });

        Button btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                StringBuilder validationErrorMessage =
                        new StringBuilder(getResources().getString(R.string.error_intro));
                validationError = false;
                if (rdbtnYes.isChecked()) {
                    dName = currentUser.getString("FirstName");
                    dLastName = currentUser.getString("LastName");
                }
                else if (rdbtnNo.isChecked()) {
                    if (isEmpty(txtName)) {
                        validationError = true;
                        txtName.requestFocus();
                        txtName.setError(getResources().getString(R.string.error_field_required));
                    }
                    if (isEmpty(txtLastName)) {
                        validationError = true;
                        txtLastName.requestFocus();
                        txtLastName.setError(getResources().getString(R.string.error_field_required));
                    }
                    dName = txtName.getText().toString();
                    dLastName = txtLastName.getText().toString();
                }
                if (rdbtnUseGPS.isChecked()){//take location from GPS (current user location)
                    locationGPS = new GPSLocation(getActivity());
                    geoPoint = new ParseGeoPoint(locationGPS.getLatitude(), locationGPS.getLongitude());//
                }
//                else if (!rdbtnUseGPS.isChecked()) {
//                    locationMap = new MapActivity();
//                    geoPoint = new ParseGeoPoint(locationMap.getPosition().getLatitude(), locationMap.getPosition().getLongitude());
//                }
               /* else {
                    validationError = true;
                }*/

                if (validationError) {
                    Toast.makeText(getActivity().getBaseContext(), validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Are you sure to submit this report?");
                    // set message
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setHint("Title (*) ");
                    builder.setView(input);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Submit",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    if (isEmpty(input)) {
                                        input.requestFocus();
                                        input.setError(getResources().getString(R.string.error_field_required));
                                        Toast.makeText(getActivity(), input.getHint() + "is required.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        title = input.getText().toString();
                                        //ProgressDialog ddialog = ProgressDialog.show(getActivity(), "Loading...", "Please wait...", true);
                                        submitReport();
                                    }
                                    // finish the application
                                    //getActivity().finish();

                                }
                            });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // TODO Auto-generated method stub
                            // close the dialog box
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    // Show Alert Dialog
                    alert.show();
                }
            }
        });
        return rootView;
    }

    private void goBackToQuestions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Answer all Home Profile questions before submitting any Reports.");
        // set message
        builder.setCancelable(false);
        builder.setPositiveButton("Take me there now",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        // Show Alert Dialog
        alert.show();
    }

    public void submitReport() {
        final ProgressDialog dlg = new ProgressDialog(getActivity());
        dlg.setTitle("Please wait.");
        dlg.setMessage("Sending Report. . .");
        dlg.show();
        id = currentUser.getObjectId();//get userID
        Log.d("Code here", id);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                post();
                dlg.dismiss();
            }
        }, 5000);
    }

    private void post() { // 1
        Report sendReport = new Report();
        //sendReport.put("UserID", ParseUser.getCurrentUser());
        sendReport.setID(currentUser);
        sendReport.setUserName(currentUser.getString("FirstName") +
                " " + currentUser.getString("LastName"));
        sendReport.setTitle(title);
        sendReport.setDriverName(dName + " " + dLastName);
        sendReport.setLocation(geoPoint);
        sendReport.saveInBackground();
        Toast.makeText(getActivity().getBaseContext(), "Report Sent!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
}