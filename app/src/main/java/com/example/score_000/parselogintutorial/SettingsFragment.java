package com.example.score_000.parselogintutorial;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class SettingsFragment extends Fragment {

    public SettingsFragment() {
    }

    ParseUser currentUser = ParseUser.getCurrentUser();// Retrieve current user from Parse.com


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        final String email = currentUser.getEmail();
        final String hiddenEmail = email.substring(0 ,3) + "****" + email.substring(email.indexOf('@'), email.length());
        Button btnChngPass = (Button) rootView.findViewById(R.id.btnChngPass);
        btnChngPass.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setTitle("An email will be sent at " + hiddenEmail + ". Continue?");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        try {
                            currentUser.requestPasswordReset(email);
                            Toast.makeText(getActivity().getBaseContext(), "Email sent.", Toast.LENGTH_LONG).show();
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
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
        return rootView;
    }
}