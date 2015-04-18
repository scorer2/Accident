package com.example.score_000.parselogintutorial;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubmitReportFragment extends Fragment {

    public ParseGeoPoint geoPoint;
    private GPSLocation locationGPS;
    private String id;
    String dName;
    String dLastName;
    String title;
    String description;
    Date time;
    int year;
    int month;
    int day;
    int hour;
    int minute;
    int count;
    long phoneNumber;
    boolean validationError = false;
    boolean towing = false;
    double latitude;
    double longitude;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int RESULT_LOAD_IMAGE = 1;
    ImageView myPhoto;
    Bitmap photo;
    ParseFile photoFile;

    public SubmitReportFragment() {
    }

    ParseUser currentUser = ParseUser.getCurrentUser();// Retrieve current user from Parse.com
    ArrayList<String> questions = (ArrayList<String>) currentUser.get("Questions");
    String typeOfPlan = currentUser.getString("PlanType");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_submit_report, container, false);

        final String planTypeArray[] = getResources().getStringArray(R.array.insuranceArray);
        final RadioButton rdbtnYes = (RadioButton) rootView.findViewById(R.id.rdbtnYes);
        final RadioButton rdbtnNo = (RadioButton) rootView.findViewById(R.id.rdbtnNo);
        final RadioButton rdbtnUseGPS = (RadioButton) rootView.findViewById(R.id.rdbtnUseGPS);
        final RadioButton rdbtnNow = (RadioButton) rootView.findViewById(R.id.rdbtnNow);
        final RadioButton rdbtnSlcDtTm = (RadioButton) rootView.findViewById(R.id.rdbtnSlcDtTm);
        Button btnLaunchMap = (Button) rootView.findViewById(R.id.btnLaunchMap);
        Button btnSubmit = (Button) rootView.findViewById(R.id.btnSubmit);
        myPhoto = (ImageView) rootView.findViewById(R.id.imgPicture);
        final EditText txtName = (EditText) rootView.findViewById(R.id.txtName);
        final EditText txtLastName = (EditText) rootView.findViewById(R.id.txtLastName);
        final EditText txtPhoneNumber = (EditText) rootView.findViewById(R.id.txtPhone);
        final EditText txtDescription = (EditText) rootView.findViewById(R.id.txtDescription);
        final CheckBox chckBoxTow = (CheckBox) rootView.findViewById(R.id.chckBoxTow);
        ParseQuery<Car> query = ParseQuery.getQuery(Car.class);
        ParseQuery innerQuery = new ParseQuery("_User");
        innerQuery.whereEqualTo("objectId", currentUser.getObjectId());
        query.whereMatchesQuery("UserID", innerQuery);
        try {
            count = query.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        if (questions != null && count != 0) { //check if user filled all questions and registered car
            if (questions.size() < 2) {
                goBackToQuestions();
            }
        } else {
            goBackToQuestions();
        }

        btnLaunchMap.setOnClickListener(new View.OnClickListener() {//1.2 open map

            @Override
            public void onClick(View view) {
                rdbtnUseGPS.setChecked(false);
                startActivity(new Intent(getActivity(), MapActivity.class));
            }
        });//open map

        rdbtnNow.setOnClickListener(new View.OnClickListener() {//2.1 check time now
            @Override
            public void onClick(View view) {
                rdbtnSlcDtTm.setChecked(false);
            }
        });//Select time now

        rdbtnSlcDtTm.setOnClickListener(new View.OnClickListener() {//2.2 check user set time
            @Override
            public void onClick(View view) {
                rdbtnSlcDtTm.setChecked(false);
                DatePickerDialog myDatePicker;
                myDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                        year = selectedYear;
                        month = selectedMonth;
                        day = selectedDay;

                        TimePickerDialog myTimePicker;
                        myTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                hour = selectedHour;
                                minute = selectedMinute;
                                c.set(year, month, day, hour, minute);
                                Toast.makeText(getActivity(), "Date and Time are set.", Toast.LENGTH_SHORT).show();
                                rdbtnNow.setChecked(false);
                                rdbtnSlcDtTm.setChecked(true);
                            }
                        }, hour, minute, true);
                        myTimePicker.show();
                        myTimePicker.setTitle("Select Time");
                    }
                }, year, month, day);
                myDatePicker.show();
                myDatePicker.setTitle("Select Date");
            }
        });//Select a different time

        myPhoto.setOnClickListener(new View.OnClickListener() {//3 click on photo
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("What do you want to do?");
                // set message
                builder.setCancelable(false);
                builder.setPositiveButton("Browse..",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                browseGallery();//upload photo by choosing an existing one from gallery
                            }
                        });

                builder.setNegativeButton("Take Photo",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                launchCamera();//user phone camera and get a new photo
                            }
                        });

                builder.setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                // Show Alert Dialog
                alert.show();
            }
        });//Cancel/ Take Photo/ Browse

        rdbtnYes.setOnClickListener(new View.OnClickListener() {//4.1 check user is driver

            @Override
            public void onClick(View view) {
                rdbtnNo.setChecked(false);
                txtName.setVisibility(View.GONE);
                txtLastName.setVisibility(View.GONE);
            }
        });//user is driver

        rdbtnNo.setOnClickListener(new View.OnClickListener() {//4.2 check user is not driver
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
                } else {
                    txtName.setVisibility(View.VISIBLE);
                    txtLastName.setVisibility(View.VISIBLE);
                }
            }
        });//different driver

        btnSubmit.setOnClickListener(new View.OnClickListener() {//8 submitting

            public void onClick(View v) {
                String validationErrorMessage = "";
                validationError = false;

                MainActivity activity = (MainActivity) getActivity();
                latitude = activity.getLat();
                longitude = activity.getLng();

                /*Checking Validations Before sending Report*/
                //1. Check Location
                if (rdbtnUseGPS.isChecked()) {//1.1 take location from GPS (current user location)
                    locationGPS = new GPSLocation(getActivity());
                    geoPoint = new ParseGeoPoint(locationGPS.getLatitude(), locationGPS.getLongitude());//
                } else if (latitude != .0) {
                    geoPoint = new ParseGeoPoint(latitude, longitude);//1.2 take location from map
                } else {
                    validationError = true;//1.3 else something went wrong
                    validationErrorMessage = "Location";
                }

                //2. Report time check
                if (rdbtnNow.isChecked()) {//2.1 get current time
                    time = Calendar.getInstance().getTime();
                } else if (rdbtnSlcDtTm.isChecked()) {//2.2 get user set time
                    time = c.getTime();
                }

                //3. Photo Check
                if (photo == null) {//3.1 error if no photo is made or selected
                    validationError = true;
                    validationErrorMessage = "Please take a photo.";
                } else {//3.2 photo is made or selected
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte[] scaledData = stream.toByteArray();
                    photoFile = new ParseFile("file" + day + minute + ".jpg", scaledData);
                }

                //4. Driver check
                if (rdbtnYes.isChecked()) {//4.1 user is the driver
                    dName = currentUser.getString("FirstName");
                    dLastName = currentUser.getString("LastName");
                } else if (rdbtnNo.isChecked()) {//4.2 user is not the driver
                    if (isEmpty(txtName)) {//4.2.1 driver first name empty - error
                        validationError = true;
                        txtName.requestFocus();
                        txtName.setError(getResources().getString(R.string.error_field_required));
                    }
                    if (isEmpty(txtLastName)) {//4.2.2 driver last name empty - error
                        validationError = true;
                        txtLastName.requestFocus();
                        txtLastName.setError(getResources().getString(R.string.error_field_required));
                    }
                    dName = txtName.getText().toString();//4.2.3 driver first name and
                    dLastName = txtLastName.getText().toString();//last name are registered
                }

                //5. Phone number check
                if (isEmpty(txtPhoneNumber)) {//5.1 phone number empty - error
                    validationError = true;
                    txtPhoneNumber.requestFocus();
                    txtPhoneNumber.setError(getResources().getString(R.string.error_field_required));
                } else
                    phoneNumber = Long.parseLong(txtPhoneNumber.getText().toString());//5.2 phone registered

                //6. Towing check
                if (chckBoxTow.isChecked())//6.1 yes towing if checked - by default it is unchecked
                    towing = true;// and towing is set to false by default

                //7. Description check
                if (isEmpty(txtDescription)) {//7.1 if text is empty - error
                    validationError = true;
                    txtDescription.requestFocus();
                    txtDescription.setError(getResources().getString(R.string.error_field_required));
                } else
                    description = txtDescription.getText().toString();//7.2 else register description

                if (validationError) {//if any error display error
                    Toast.makeText(getActivity().getBaseContext(), validationErrorMessage, Toast.LENGTH_LONG).show();
                    return;
                } else {//no error - proceed with report
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

                                    if (isEmpty(input)) {//if title empty - error
                                        input.requestFocus();
                                        input.setError(getResources().getString(R.string.error_field_required));
                                        Toast.makeText(getActivity(), input.getHint() + "is required.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        title = input.getText().toString();//get title and submit
                                        submitReport();
                                    }
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

    public void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void browseGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data.getAction() == null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getActivity().getBaseContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            photo = BitmapFactory.decodeFile(picturePath);
            myPhoto.setImageBitmap(photo);//displays the photo on upper right corner
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            photo = (Bitmap) extras.get("data");
            myPhoto.setImageBitmap(photo);//displays the photo on upper right corner
        }
    }

    private void goBackToQuestions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Complete Profile before submitting any Reports.");
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
        Report report = new Report();
        report.setID(currentUser);
        report.setUserName(currentUser.getString("FirstName") +
                " " + currentUser.getString("LastName"));
        report.setTitle(title);
        report.setDriverName(dName + " " + dLastName);
        report.setLocation(geoPoint);
        report.setTime(time);
        report.setPhoto(photoFile);
        report.setPhoneNumber(phoneNumber);
        report.setTowing(towing);
        report.setHidden(false);
        report.setDescription(description);
        report.saveInBackground();
        Toast.makeText(getActivity().getBaseContext(), "Report Sent!", Toast.LENGTH_LONG).show();

        ParsePush push = new ParsePush();
        push.setChannel("Reports");
        push.setMessage("You have just received a new report");
        push.sendInBackground();

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