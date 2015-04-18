package com.example.score_000.parselogintutorial;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecentReportsFragment extends Fragment {
    private List<Report> myReports = new ArrayList<Report>();
    private List<Report> currentReport = new ArrayList<Report>(1);
    int pos;
    List<String> users = new ArrayList<String>();
    List<String> titles = new ArrayList<String>();
    List<String> drivers = new ArrayList<String>();
    List<String> dates = new ArrayList<String>();
    List<String> times = new ArrayList<String>();
    List<ParseGeoPoint> locations = new ArrayList<ParseGeoPoint>();
    List<Boolean> towing = new ArrayList<Boolean>();
    List<String> descriptions = new ArrayList<String>();
    List<ParseFile> pictures = new ArrayList<ParseFile>();
    List<Boolean> hidden = new ArrayList<Boolean>();

    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    final ParseQuery<Report> query = ParseQuery.getQuery(Report.class);
    ParseUser currentUser = ParseUser.getCurrentUser();
    ParseQuery innerQuery = new ParseQuery("_User");

    public RecentReportsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recent_reports, container, false);
        bringReports();
        return rootView;
    }

    public void bringReports() {

        try {
            if (currentUser.getString("Role").equals("user")){
                innerQuery.whereEqualTo("objectId", currentUser.getObjectId());//find reports for current user
                query.whereMatchesQuery("UserID", innerQuery);
                query.whereEqualTo("Hidden", false);//will show only those report which haven't been deleted by users
            }
            query.orderByDescending("createdAt"); //order reports by created date (most recent are on top)
            if (query.count() > 0) {
                query.findInBackground(new FindCallback<Report>() {

                    @Override
                    public void done(List<Report> list, ParseException e) {

                        for (int i = 0; i < list.size(); i++) {
                            myReports.add(new Report());
                            users.add(i, list.get(i).getUserName());
                            titles.add(i, list.get(i).getTitle());
                            drivers.add(i, list.get(i).getDriverName());
                            dates.add(i, formatter.format(list.get(i).getCreatedAt()));
                            times.add(i, formatter.format(list.get(i).getTime()));
                            locations.add(i, list.get(i).getLocation());
                            towing.add(i, list.get(i).getTowing());
                            descriptions.add(i, list.get(i).getDescription());
                            pictures.add(i, list.get(i).getPhoto());
                            hidden.add(i, list.get(i).getHidden());
                            Log.d("YES", "");
                        }
                        populateListReportView();
                        registerClickCallback();
                    }
                });
            } else
                Toast.makeText(getActivity(), "No Reports to display!", Toast.LENGTH_LONG).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateListReportView() {
        getView().findViewById(R.id.btnGoBack).setVisibility(View.GONE);
        getView().findViewById(R.id.btnClearSlc).setVisibility(View.GONE);
        getView().findViewById(R.id.btnDeleteSlc).setVisibility(View.GONE);
        // Create list of items
        ArrayAdapter<Report> adapter = new MyListAdapter();
        ListView listReports = (ListView) getView().findViewById(R.id.listReportView);
        listReports.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Report> {
        public MyListAdapter() {
            super(getActivity(), R.layout.main_report_view, myReports);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.main_report_view, parent, false);
            }
            //Fill The Title
            TextView titleText = (TextView) itemView.findViewById(R.id.txtReportView);
            titleText.setText(position + 1 + ". " + titles.get(position));

            //Fill the Date
            TextView dateText = (TextView) itemView.findViewById(R.id.txtDate);
            dateText.setText(dates.get(position));

            //Fill DriverName
            TextView driverText = (TextView) itemView.findViewById(R.id.txtDriverName);
            driverText.setText(drivers.get(position));

            return itemView;
        }
    }

    public void registerClickCallback() {
        ListView listReports = (ListView) getView().findViewById(R.id.listReportView);
        listReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                currentReport.clear();
                getView().findViewById(R.id.btnClearSlc).setVisibility(View.GONE);
                getView().findViewById(R.id.btnDeleteSlc).setVisibility(View.GONE);
                Button btnBack = (Button) getView().findViewById(R.id.btnGoBack);
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //go back to Reports
                        populateListReportView();
                    }
                });
                pos = position;
                currentReport.add(new Report());
                populateReportView();
            }
        });

        listReports.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View viewClicked, final int position, long id) {
                //delete() or hide() report;
                viewClicked.setBackgroundColor(Color.BLUE);

                final Button btnClearSlc = (Button) getView().findViewById(R.id.btnClearSlc);
                btnClearSlc.setVisibility(View.VISIBLE);
                btnClearSlc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        populateListReportView();
                    }
                });

                final Button btnDeleteSlc = (Button) getView().findViewById(R.id.btnDeleteSlc);
                btnDeleteSlc.setVisibility(View.VISIBLE);
                btnDeleteSlc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewClicked.setVisibility(View.INVISIBLE);
                        myReports.get(position).setHidden(true);//"deletes the selected report" (not from database though)
                        query.whereMatches("Title", titles.get(position));//only 1 item will be returned
                        query.getFirstInBackground(new GetCallback<Report>() {
                            @Override
                            public void done(Report report, ParseException e) {
                                if (e == null) {
                                    report.setHidden(true);
                                    report.saveInBackground();
                                }
                            }
                        });
                        btnDeleteSlc.setVisibility(View.GONE);
                        btnClearSlc.setVisibility(View.GONE);
                    }
                });

                return false;
            }
        });
    }

    private void populateReportView() {
        ArrayAdapter<Report> adapter = new MyListAdapter2();
        ListView listReports = (ListView) getView().findViewById(R.id.listReportView);
        listReports.setAdapter(adapter);
    }

    private class MyListAdapter2 extends ArrayAdapter<Report> {
        public MyListAdapter2() {
            super(getActivity(), R.layout.single_report_view, currentReport);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.single_report_view, parent, false);
            }
            position = pos;
            //0. Fill User
            TextView userText = (TextView) itemView.findViewById(R.id.txt_single_user);
            userText.setText(users.get(position));

            //1. Fill The Title
            TextView titleText = (TextView) itemView.findViewById(R.id.single_txt_title);
            titleText.setText(titles.get(position));

            //2. Fill DriverName
            TextView driverText = (TextView) itemView.findViewById(R.id.single_txt_driver);
            driverText.setText(drivers.get(position));

            //3. Fill the Date Created
            TextView dateText = (TextView) itemView.findViewById(R.id.single_txt_date);
            dateText.setText(dates.get(position));

            //4. Fill the Time of Accident
            TextView timeText = (TextView) itemView.findViewById(R.id.single_txt_time);
            timeText.setText(times.get(position));

            //5. Fill Location
            TextView locText = (TextView) itemView.findViewById(R.id.single_txt_location);
            locText.setText("Location: " + locations.get(position).getLatitude() +
                    ", " + locations.get(position).getLongitude());
            locText.setAutoLinkMask(Linkify.WEB_URLS);

            //6. Fill Towing Service
            TextView towingText = (TextView) itemView.findViewById(R.id.single_txt_towing1);
            towingText.setText(towing.get(position).toString());

            //7. Fill Description
            TextView descriptionText = (TextView) itemView.findViewById(R.id.single_txt_Description);
            descriptionText.setText(descriptions.get(position));

            //8. Fill Picture
            final ImageView imgView = (ImageView) itemView.findViewById(R.id.single_imgView);
            if (pictures.get(position) != null) {
                pictures.get(position).getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            Bitmap myReportPic = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imgView.setImageBitmap(myReportPic);
                        }
                    }
                });
            }

            return itemView;
        }
    }

    public String getCount() throws ParseException {
        return Integer.toString(query.count() - 4);
    }
}