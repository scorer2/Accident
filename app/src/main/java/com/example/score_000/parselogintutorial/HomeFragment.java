package com.example.score_000.parselogintutorial;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {
    public HomeFragment() {
    }


    private Spinner planSpinner;
    private int selectedPlan = 0;
    int currentSelection = 0;
    ParseUser currentUser = ParseUser.getCurrentUser();// Retrieve current user from Parse.com
    String planType = currentUser.getString("PlanType");
    int year, month, day;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

            final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress1);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(0);
            progressBar.setMax(4);

            String struser = currentUser.getString("FirstName");// Convert currentUser into String
            TextView txtuser = (TextView) rootView.findViewById(R.id.mytextview);
            //Buttons for the questions
            final Button btnslctBday = (Button) rootView.findViewById(R.id.btnSlctBday);
            final Button btnentLicence = (Button) rootView.findViewById(R.id.btnLicence);
            final Button btnslcBloodType = (Button) rootView.findViewById(R.id.btnBloodType);

            // Set the currentUser String into TextView
            txtuser.setText("Hello, " + struser);
            final String planTypeArray[] = getResources().getStringArray(R.array.insuranceArray);

            //spinner - DropDown list
            planSpinner = (Spinner) rootView.findViewById(R.id.spinner1);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                    R.array.insuranceArray, android.R.layout.simple_spinner_item);

            // simple_spinner_dropdown_item is basically a single line TextView that serves as header of a Spinner
            adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

            // set array adapter to populate the spinner
            planSpinner.setAdapter(adapter);

            // add listener that will react when an item was selected

            for (currentSelection = 0; currentSelection < planTypeArray.length; currentSelection++) {
                if (planType == null || planType.equals("")) {
                    planSpinner.setSelection(0);
                    break;
                } else if (planType.equals(planTypeArray[currentSelection])) {
                    planSpinner.setSelection(currentSelection);
                    progressBar.setProgress(1);
                    break;
                }
            }

            planSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {


                    TextView insurancePlan = (TextView) rootView.findViewById(R.id.txtPlan);
                    // Set the current insurance plan into TextView

                    insurancePlan.setText("My current plan is: ");// + currentUser.getString("PlanType"));
                    if (selectedPlan == 0) {
                        selectedPlan++;
                    } else if (position == 0) {
                        Toast.makeText(getActivity().getBaseContext(), "Please choose.", Toast.LENGTH_LONG).show();
                        planSpinner.setSelection(currentSelection);
                    } else if (position == 3) {
                        Toast.makeText(getActivity().getBaseContext(), "Cannot update plan to '" +
                                planTypeArray[position] + ".' Contact customer support.", Toast.LENGTH_LONG).show();
                        planSpinner.setSelection(currentSelection);
                    } else if (currentSelection != position) {
                        currentUser.put("PlanType", planTypeArray[position]);
                        currentUser.saveInBackground();//update the plan
                        // we would not have debug toasts in production version, for edu purposes only
                        Toast.makeText(getActivity().getBaseContext(), "Plan successfully updated to '" +
                                planTypeArray[position] + ".'", Toast.LENGTH_LONG).show();
                        currentSelection = position;
                        if (progressBar.getProgress() == 0) {
                            progressBar.setProgress(1);
                            btnslctBday.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // onNothingSelected is a Callback method to be invoked when the selection disappears from this view.
                    // The selection can disappear for instance when touch is activated or when the adapter becomes empty.
                    Toast.makeText(getActivity().getBaseContext(), "onNothingSelected", Toast.LENGTH_LONG).show();
                }
            });

            ArrayList<String> questions = (ArrayList<String>) currentUser.get("Questions");
            String question1 = null;
            String question2 = null;

            if (questions != null) {
                question1 = questions.get(0);
                if (questions.size() == 2) {
                    question2 = questions.get(1);
                }
            }

            Date birth_date = currentUser.getDate("Birth_Date");
            if (currentSelection != 0) {
                if (birth_date == null) {
                    btnslctBday.setVisibility(View.VISIBLE);
                } else if (question1 == null) {
                    btnentLicence.setVisibility(View.VISIBLE);
                    progressBar.setProgress(2);
                } else if (question2 == null) {
                    btnslcBloodType.setVisibility(View.VISIBLE);
                    progressBar.setProgress(3);
                } else {
                    progressBar.setProgress(4);
                }
            }

            btnslctBday.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // create instance of calendar
                    final Calendar c = Calendar.getInstance();
                    // get year, month and day
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                    //DatePickerDialog(context,callBack,year,monthOfYear,dayOfMonth)
                    // Set DatePicker Listener always called when Set Button is clicked
                    new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                        // this method will call on close dialog box
                        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {

                            year = selectedYear;
                            month = selectedMonth;
                            day = selectedDay;

                            c.set(Calendar.MONTH, month);
                            c.set(Calendar.DATE, day);
                            c.set(Calendar.YEAR, year);
                            c.set(Calendar.HOUR, 0);
                            c.set(Calendar.MINUTE, 0);
                            c.set(Calendar.SECOND, 0);

                            currentUser.put("Birth_Date", c.getTime());
                            currentUser.saveInBackground();//update birthdate
                            String fullBirthdate = String.valueOf(year) + "-" + (month + 1) + "-" + day;
                            Toast.makeText(getActivity().getBaseContext(), "Birthdate set: " + fullBirthdate, Toast.LENGTH_LONG).show();
                            getActivity().getBaseContext();

                            btnslctBday.setVisibility(View.GONE);
                            btnentLicence.setVisibility(View.VISIBLE);
                            progressBar.setProgress(2);
                        }
                    }, year, month, day).show();
                }
            });

            btnentLicence.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    // Create instance of Alert Dialog Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please enter your Licence number");
                    // set message
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Save",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    if (!input.getText().toString().isEmpty()) {

                                        currentUser.add("Questions", input.getText().toString());
                                        currentUser.saveInBackground();
                                        Toast.makeText(getActivity().getBaseContext(), "Licence updated.", Toast.LENGTH_LONG).show();
                                        btnentLicence.setVisibility(View.GONE);
                                        btnslcBloodType.setVisibility(View.VISIBLE);
                                        progressBar.setProgress(3);
                                    } else {
                                        input.setText("Can't be empty");
                                        Toast.makeText(getActivity().getBaseContext(), "Field Can't be empty.", Toast.LENGTH_LONG).show();

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

                    //create instance of alert dialogand assign configuration of
                    //builderto alert dialog instance

                    AlertDialog alert = builder.create();
                    // Show Alert Dialog
                    alert.show();
                }
            });

            btnslcBloodType.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    // Create instance of Alert Dialog Builder
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    // create array of items basic colors RGB (Red,Green,Blue)
                    final String bloodTypeArray[] = getResources().getStringArray(R.array.bloodType);
                    // set title
                    builder.setTitle("Make sure you know your blood type!");
                    builder.setItems(bloodTypeArray, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int k) {
                            // TODO Auto-generated method stub
                            currentUser.add("Questions", bloodTypeArray[k]);
                            currentUser.saveInBackground();
                            Toast.makeText(getActivity().getBaseContext(), "Blood type: " + bloodTypeArray[k], Toast.LENGTH_LONG).show();
                            btnslcBloodType.setVisibility(View.GONE);
                            progressBar.setProgress(4);
                        }
                    });

                    //create instance of alert dialogand assign configuration of
                    //builderto alert dialog instance

                    AlertDialog alert = builder.create();
                    // Show Dialog
                    alert.show();
                }
            });
        return rootView;
    }
}