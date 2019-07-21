package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

public class FirstActivity extends AppCompatActivity {

    ListView linearLayoutListView;
    TextView url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Intent intent = getIntent();
        String magnitude = intent.getStringExtra("magnitude");
        String date2 = intent.getStringExtra("date");
        String order = intent.getStringExtra("order");
        String stringUrl = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" + date2 + "&minmagnitude=7.0&limit=" + magnitude + "&orderby=" + order;

        QuakeAsyncTask task = new QuakeAsyncTask();
        task.execute(stringUrl);
    }

    class QuakeAsyncTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... stringUrl) {
            return Utils.fetchEarthquakeData(stringUrl[0]);
        }

        public void onPostExecute(List<String> postExecuteResult) {
            CustomListAdapter arrayAdapter = new CustomListAdapter(FirstActivity.this, postExecuteResult);
            linearLayoutListView = findViewById(R.id.listViewManyItemsID);
            linearLayoutListView.setAdapter(arrayAdapter);
            linearLayoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String lat = ((TextView) view.findViewById(R.id.textViewLat)).getText().toString();
                    String log = ((TextView) view.findViewById(R.id.textViewLog)).getText().toString();

                    Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(
                            "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + log + "#map=5/" + lat + "/" + log));
                    startActivity(intent);
                }
            });
        }
    }
}

class CustomListAdapter extends ArrayAdapter<String> {
    Activity context;
    List<String> itemName1;

    public CustomListAdapter(Activity activity, List<String> itemNameA) {
        super(activity, R.layout.text_view1, itemNameA);
        this.context = activity;
        this.itemName1 = itemNameA;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.text_view1, null, true);
        String earthInfo[] = itemName1.get(position).split("@@");
        TextView title = rowView.findViewById(R.id.textViewInfo);
        TextView date = rowView.findViewById(R.id.textViewDate);
        TextView url = rowView.findViewById(R.id.textViewUrl);
        TextView mag = rowView.findViewById(R.id.textViewMag);
        TextView log = rowView.findViewById(R.id.textViewLog);
        TextView lat = rowView.findViewById(R.id.textViewLat);


        title.setText(earthInfo[0]);

        date.setText(new Date(Long.parseLong(earthInfo[1])).toString());
        url.setText(earthInfo[2]);
        mag.setText(earthInfo[3]);
        log.setText(earthInfo[4]);
        lat.setText(earthInfo[5]);


        double number = Double.parseDouble(mag.getText().toString());
        System.out.print(number);
        if (number > 7.4) {
            title.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
            date.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
        }
        if (number < 7.5) {
            title.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
            date.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
        }
        return rowView;
    }
}
