package com.example.score_000.parselogintutorial;


import android.app.Fragment;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RecentReportsFragment extends Fragment {
    private List<Report> myReports = new ArrayList<Report>();
    List<String> myItems = new ArrayList<String>();
    List<ParseGeoPoint> locations = new ArrayList<ParseGeoPoint>();
    List<String> drivers = new ArrayList<String>();
    List<String> titles = new ArrayList<String>();
    List<String> dates = new ArrayList<String>();
    List<String> descriptions = new ArrayList<String>();
    List<Picture> pictures = new ArrayList<Picture>();

   // String[] myItems = new String[3]; // Build Adapter
    SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss");
    final ParseQuery<Report> query = ParseQuery.getQuery(Report.class);
    ParseUser currentUser = ParseUser.getCurrentUser();
    //registerClickCallback();
    ParseQuery innerQuery = new ParseQuery("_User");

    public RecentReportsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recent_reports, container, false);
        //innerQuery.whereEqualTo("objectId", currentUser.getObjectId() );//find reports for current user
     //   query.whereMatchesQuery("UserID", innerQuery);
      //  query.orderByDescending("createdAt"); //order reports by created date (the recent are on top)
        bringReports();
        return rootView;
    }

    public void bringReports(){

        try {
            innerQuery.whereEqualTo("objectId", currentUser.getObjectId() );//find reports for current user
            query.whereMatchesQuery("UserID", innerQuery);
            query.orderByDescending("createdAt"); //order reports by created date (the recent are on top)
            if (query.count() > 0) {
                query.findInBackground(new FindCallback<Report>() {


                    @Override
                    public void done (List<Report> list, ParseException e) {

                        //{
                        for (int i = 0; i < list.size(); i++) {
                            //rep.getID();
                            //myItems.add(i, list.get(i).getTitle() + " - " + formatter.format(list.get(i).getCreatedAt()));
                            myReports.add(new Report());//list.get(i).getTitle()));//, list.get(i).getDriverName()));
                            titles.add(i, list.get(i).getTitle());
                            dates.add(i, formatter.format(list.get(i).getCreatedAt()));
                            locations.add(i, list.get(i).getLocation());
                            drivers.add(i, list.get(i).getDriverName());
                            //descriptions.add(i, list.get(i).getDescription);
                            //pictures.add(i, list.get(i).getPictures);
                            Log.d("YES", "");
                        }
                        //  }
                        populateListReportView();
                        registerClickCallback();
                    }
                });
            }
            else
                Toast.makeText(getActivity(),"No Reports to display!",Toast.LENGTH_LONG).show();
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void populateListReportView() {
        getView().findViewById(R.id.btnGoBack).setVisibility(View.GONE);
        // Create list of items
        ArrayAdapter<Report> adapter = new MyListAdapter();
        ListView listReports = (ListView) getView().findViewById(R.id.listReportView);
        listReports.setAdapter(adapter);

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), R.layout.report_view, myItems);
        ListView listReports = (ListView) getView().findViewById(R.id.listReportView);
        listReports.setAdapter(adapter);*/
    }

    private class MyListAdapter extends ArrayAdapter<Report> {
        public MyListAdapter(){
            super(getActivity(), R.layout.main_report_view, myReports);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View itemView = convertView;
            if(itemView==null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.main_report_view, parent, false);
            }
            //find Reports to work with
            Report currentReport = myReports.get(position);

            //Fill The Title
            TextView titleText = (TextView) itemView.findViewById(R.id.txtReportView);
            //titleText.setText(currentReport.getTitle());
            titleText.setText(position + 1 + ". " + titles.get(position));

            //Fill the Date
            TextView dateText = (TextView) itemView.findViewById(R.id.txtDate);
            dateText.setText(dates.get(position));

            //Fill DriverName
            TextView driverText = (TextView) itemView.findViewById(R.id.txtDriverName);
            driverText.setText(drivers.get(position));

            //Fill Location
            TextView locText = (TextView) itemView.findViewById(R.id.txtLocation);
            //locText.setText(locations.get(position).getLatitude() + ", " + locations.get(position).getLongitude());

            return itemView;
        }
    }

    private void registerClickCallback() {
        final ListView listReports = (ListView) getView().findViewById(R.id.listReportView);
        listReports.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, final int position, long id) {
                final Button btnBack = (Button) getView().findViewById(R.id.btnGoBack);
                btnBack.setVisibility(View.VISIBLE);
                btnBack.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                            // bringReports();//go back to Reports
                            populateListReportView();
                         }
                      });

                String[] showLoc = new String[1];
                showLoc[0] = locations.get(position).getLatitude() + ", " + locations.get(position).getLongitude();
                TextView locText = (TextView) getView().findViewById(R.id.txtLocation);
               // locText.setText(

//                        (Html.fromHtml(
  //                              "<a href=\"http://www.google.com\">Location</a>"));

                //locText.setMovementMethod(LinkMovementMethod.getInstance());
                //locText.setText(showLoc[0].toString());
               // ArrayAdapter<Report> adapter = new MyListAdapter();
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.report_view, showLoc);
                listReports.setAdapter(adapter);
               // startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });
    }

    public String getCount() throws ParseException {
        return Integer.toString(query.count() - 2);
    }
}