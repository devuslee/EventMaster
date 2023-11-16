package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventmaster.R;

import java.util.ArrayList;
import java.util.List;

public class Overview extends AppCompatActivity {

    private Context context;
    private DBHelper DB;

    private ListView eventlist;
    private TextView countevents;
    private Button add;

    private List<Event> events;
    private ImageView home, event, favourite, setting, overviewbutton, calendarbutton;

    int userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        context = this;
        DB = new DBHelper(context);

        eventlist = (ListView) findViewById(R.id.eventlist);


        int userID = getIntent().getIntExtra("userID", 0);

        events = new ArrayList<>();
        events = DB.getAllFavourite(userID);

        EventsAdapter adapter = new EventsAdapter(context, R.layout.activity_home, events, DB);
        eventlist.setAdapter(adapter);

        eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Event event = events.get(i);
                Intent intent = new Intent(context, ViewMoreUpdate.class);
                intent.putExtra("name", event.getName());
                intent.putExtra("location", event.getLocation());
                intent.putExtra("description", event.getDescription());
                intent.putExtra("type", event.getType());
                intent.putExtra("startdatetime", event.getStartdatetime());
                intent.putExtra("enddatetime", event.getEnddatetime());
                intent.putExtra("eventimage", event.getImage());
                intent.putExtra("userID", userID);
                intent.putExtra("eventID", event.getEventID());
                startActivity(intent);

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userID = getIntent().getIntExtra("userID", 0);
                Intent intent = new Intent(context, eventHome.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        event.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventList.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Favourite.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventSetting.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
}