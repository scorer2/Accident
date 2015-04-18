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
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

    private EditText usernameView;
    private EditText passwordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        // Set up the login form.
        usernameView = (EditText) findViewById(R.id.username);
        passwordView = (EditText) findViewById(R.id.password);

        // Set up the submit button click handler
        findViewById(R.id.action_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Validate the log in data
                boolean validationError = false;
                StringBuilder validationErrorMessage =
                        new StringBuilder(getResources().getString(R.string.error_intro));
                if (isEmpty(usernameView)) {
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_username));
                    usernameView.requestFocus();
                    usernameView.setError(getResources().getString(R.string.error_field_required));
                }
                if (isEmpty(passwordView)) {
                    if (validationError) {
                        // validationErrorMessage.append(getResources().getString(R.string.error_join));
                    }
                    validationError = true;
                    //validationErrorMessage.append(getResources().getString(R.string.error_blank_password));
                    passwordView.requestFocus();
                    passwordView.setError(getResources().getString(R.string.error_field_required));
                }
                validationErrorMessage.append(getResources().getString(R.string.error_end));

                // If there is a validation error, display the error
                if (validationError) {
                    Toast.makeText(LoginActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG).show();
                    return;
                }

                // Set up a progress dialog
                final ProgressDialog dlg = new ProgressDialog(LoginActivity.this);
                dlg.setTitle("Please wait...");
                dlg.setMessage("Logging in.  Please wait...");
                dlg.show();
                // Call the Parse login method
                ParseUser.logInInBackground(usernameView.getText().toString(), passwordView.getText()
                        .toString(), new LogInCallback() {

                    @Override
                    public void done(ParseUser user, ParseException e) {
                        dlg.dismiss();
                        if (e != null) {
                            // Show the error message
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            // Start an intent for the dispatch activity
                            Intent intent = new Intent(LoginActivity.this, DispatchActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        TextView t1 = (TextView) findViewById(R.id.txtForgot);
        t1.setText(
                Html.fromHtml(
                        "<b>Forgot</b> " +
                                "<a href=\"\"><i>Username/Password</i></a>"));
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Verify your email.");
                final EditText input = new EditText(LoginActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        //      if (input.getText().toString() == ParseUser.getCurrentUser().getEmail()) {
                        try {
                            ParseUser.requestPasswordReset(input.getText().toString());
                            Toast.makeText(LoginActivity.this, "Email sent.", Toast.LENGTH_LONG).show();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Email " + input.getText() + " not found!", Toast.LENGTH_LONG).show();
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
        });

        TextView t2 = (TextView) findViewById(R.id.txtSignUpFromSignIn);
        t2.setText(
                Html.fromHtml(
                        "<b>Not a Member?</b> " +
                                "<a href=\"\"><i>Sign Up</i></a>"));
        t2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
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
}