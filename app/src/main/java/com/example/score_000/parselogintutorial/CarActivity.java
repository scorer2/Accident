package com.example.score_000.parselogintutorial;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by score_000 on 4/13/2015.
 */

public class CarActivity extends Activity {
    Boolean validationError;
    ParseUser currentUser;
    String make;
    String model;
    String color;
    String license_plate;
    String VIN;
    int year;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);
        //SearchView srchView = (SearchView) findViewById(R.id.searchView);
        final Spinner carSpinner = (Spinner) findViewById(R.id.spinner2);
        final EditText txtModel = (EditText) findViewById(R.id.txtCarModel);
        final EditText txtColor = (EditText) findViewById(R.id.txtCarColor);
        final EditText txtLicensePlate = (EditText) findViewById(R.id.txtLicensePlate);
        final EditText txtVIN = (EditText) findViewById(R.id.txtVIN);
        final EditText txtYear = (EditText) findViewById(R.id.txtCarYear);
        Button btnSaveCar = (Button) findViewById(R.id.btnSaveCar);

        currentUser = ParseUser.getCurrentUser();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.carMakeArray, android.R.layout.simple_spinner_item);
        // simple_spinner_dropdown_item is basically a single line TextView that serves as header of a Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        // set array adapter to populate the spinner
        carSpinner.setAdapter(adapter);


        carSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (view != null) {
                    make = carSpinner.getSelectedItem().toString();
                } else
                    Toast.makeText(CarActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // onNothingSelected is a Callback method to be invoked when the selection disappears from this view.
                // The selection can disappear for instance when touch is activated or when the adapter becomes empty.
                Toast.makeText(getBaseContext(), "onNothingSelected", Toast.LENGTH_LONG).show();
            }
        });

        btnSaveCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String errorMessage = "";
                validationError = false;
                if (carSpinner.getSelectedItemPosition() == 0) {
                    validationError = true;
                    errorMessage = "Select Make";
                }

                if (isEmpty(txtModel)) {
                    validationError = true;
                    txtModel.requestFocus();
                    txtModel.setError(getResources().getString(R.string.error_field_required));
                }

                if (isEmpty(txtColor)) {
                    validationError = true;
                    txtColor.requestFocus();
                    txtColor.setError(getResources().getString(R.string.error_field_required));
                }

                if (isEmpty(txtLicensePlate)) {
                    validationError = true;
                    txtLicensePlate.requestFocus();
                    txtLicensePlate.setError(getResources().getString(R.string.error_field_required));
                }

                if (isEmpty(txtVIN)) {
                    validationError = true;
                    txtVIN.requestFocus();
                    txtVIN.setError(getResources().getString(R.string.error_field_required));
                }

                if (!txtYear.getText().toString().matches("['0-9' ]+")) {
                    validationError = true;
                    txtYear.requestFocus();
                    txtYear.setError(getResources().getString(R.string.error_characters));
                }

                if (isEmpty(txtYear)) {
                    validationError = true;
                    txtYear.requestFocus();
                    txtYear.setError(getResources().getString(R.string.error_field_required));
                }

                if (validationError) {
                    Toast.makeText(CarActivity.this, "Issues found. " + errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    model = txtModel.getText().toString();
                    color = txtColor.getText().toString();
                    license_plate = txtLicensePlate.getText().toString();
                    VIN = txtVIN.getText().toString();
                    year = Integer.parseInt(txtYear.getText().toString());
                    postCar();
                }
            }
        });
    }

    private void postCar() {
        Car myCar = new Car();
        myCar.setID(currentUser);
        myCar.setMake(make);
        myCar.setModel(model);
        myCar.setYear(year);
        myCar.setColor(color);
        myCar.setLicensePlate(license_plate);
        myCar.setVIN(VIN);
        myCar.saveInBackground();
        Toast.makeText(this, "Car Registered!", Toast.LENGTH_LONG).show();
        startActivity(new Intent(CarActivity.this, MainActivity.class));
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }
}