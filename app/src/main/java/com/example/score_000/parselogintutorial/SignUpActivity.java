package com.example.score_000.parselogintutorial;

/**
 * Created by score_000 on 2/12/2015.
 */


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUpActivity extends Activity {

    private EditText firstNameView;
    private EditText lastNameView;
    private EditText emailAddressView;
    private EditText usernameView;
    private EditText passwordView;
    private EditText passwordAgainView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        // Set up the signup form.
        firstNameView = (EditText) findViewById(R.id.firstName);
        lastNameView = (EditText) findViewById(R.id.lastName);
        emailAddressView = (EditText) findViewById(R.id.emailAddress);
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);
        passwordAgainView = (EditText) findViewById(R.id.passwordAgain);


        // Set up the submit button click handler
        findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // Validate the sign up data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder(getResources().getString(R.string.error_intro));

                if (!firstNameView.getText().toString().matches("[a-zA-Z ]+")) {
                    validationError = true;
                    firstNameView.requestFocus();
                    firstNameView.setError(getResources().getString(R.string.error_characters));
                }

                if (isEmpty(firstNameView)) {
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_firstName));
                    firstNameView.requestFocus();
                    firstNameView.setError(getResources().getString(R.string.error_field_required));
                }

                if (!lastNameView.getText().toString().matches("[a-zA-Z ]+")) {
                    validationError = true;
                    lastNameView.requestFocus();
                    lastNameView.setError(getResources().getString(R.string.error_characters));
                }

                if (isEmpty(lastNameView)) {

                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_lastName));
                    lastNameView.requestFocus();
                    lastNameView.setError(getResources().getString(R.string.error_field_required));
                }

                if (isEmpty(emailAddressView)) {
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_emailAddress));
                    emailAddressView.requestFocus();
                    emailAddressView.setError(getResources().getString(R.string.error_field_required));
                }

                if (!isValidEmail(emailAddressView.getText().toString())) {
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_emailAddress));
                    emailAddressView.requestFocus();
                    emailAddressView.setError("Incorrect Email.");
                }

                if (isEmpty(usernameView)) {
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
                    usernameView.requestFocus();
                    usernameView.setError(getResources().getString(R.string.error_field_required));
                }

                if (isEmpty(passwordView)) {
                    if (validationError) {
                        //validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
                    passwordView.requestFocus();
                    passwordView.setError(getResources().getString(R.string.error_field_required));
                }

                if (!isMatching(passwordView, passwordAgainView)) {
                    if (validationError) {
                        //validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_mismatched_passwords));
                    passwordAgainView.requestFocus();
                    passwordAgainView.setError(getResources().getString(R.string.error_mismatched_passwords));
                }

                validationErrorMessage.append(getResources().getString(R.string.error_end));

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(SignUpActivity.this);
                dlg.setTitle("Please wait...");
                dlg.setMessage("Signing up.  Please wait...");
                dlg.show();

                // Set up a new Parse user
                ParseUser user = new ParseUser();
                user.setUsername(usernameView.getText().toString());
                user.setPassword(passwordView.getText().toString());
                user.setEmail(emailAddressView.getText().toString());
                user.put("FirstName", firstNameView.getText().toString());
                user.put("LastName", lastNameView.getText().toString());
                user.put("Role", "user");////

                // Call the Parse signup method
                user.signUpInBackground(new SignUpCallback() {

                    @Override
                    public void done(ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            if (e.getMessage().startsWith("u")) {
                                usernameView.requestFocus();
                                usernameView.setError(getResources().getString(R.string.error_userName_taken));
                            } else {
                                emailAddressView.requestFocus();
                                emailAddressView.setError(getResources().getString(R.string.error_invalid_email));
                            }
                        } else {
                            // Start an intent for the dispatch activity
                            Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(SignUpActivity.this, "Verification link sent to email.", Toast.LENGTH_LONG).show();
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        TextView t3 = (TextView) findViewById(R.id.txtSignInFromSignUp);

        t3.setText(
                Html.fromHtml(
                        "<b>Already a Member?</b> " +
                                "<a href=\"\"><i>Sign In</i></a>"));
        t3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0) {
            return false;
        } else {
            return true;
        }
    }

    private boolean isMatching(EditText etText1, EditText etText2) {
        if (etText1.getText().toString().equals(etText2.getText().toString())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
}