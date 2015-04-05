package com.example.score_000.parselogintutorial;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class BirthdayActivity extends Activity {
    TextView tvDemo;
    Button btnDatePickerDialog;
    int year, month, day;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.birthday_activity);

        tvDemo = (TextView) findViewById(R.id.tvDemo);

        // DatePicker Dialog Demo
        btnDatePickerDialog = (Button) findViewById(R.id.btnDatePickerDialog);
        btnDatePickerDialog.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                // create instance of calendar
                Calendar c = Calendar.getInstance();
                // get year,month and day
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                // get day of month
                day = c.get(Calendar.DAY_OF_MONTH);
                // show date picker dialog
                // DatePickerDialog(context,callBack,year,monthOfYear,dayOfMonth)
                new DatePickerDialog(BirthdayActivity.this, datePickerListener, year, month, day).show();
            }
        });
    }

    // Set DatePicker Listener always called when Set Button is clicked //
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // this method will call on close dialog box
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            // set selected date into tvDemo
            tvDemo.setText(new StringBuilder().append(day).append("-")
                    .append(month + 1).append("-").append(year).append(" "));


            startActivity(new Intent(BirthdayActivity.this, MainActivity.class));




            // set selected date into date picker dialog also
            // dpResult.init(year, month, day, null);

        }
    };
}