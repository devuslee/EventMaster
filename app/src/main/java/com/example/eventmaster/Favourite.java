package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmanager.R;

import java.util.ArrayList;
import java.util.List;

public class Favourite extends AppCompatActivity {

    private Button calendarbutton, overviewbutton;

    private ImageView home,notification,favourite,setting, search;
    private EditText searchtext;
    private Context context;
    private DBHelper DB;

    private ListView eventlist;
    private TextView countevents, noevents;

    private List<Event> events;

    private SearchView searchview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        String tempevent = getIntent().getStringExtra("name");
        String templocation = getIntent().getStringExtra("location");
        String tempdescription = getIntent().getStringExtra("description");
        String temptype = getIntent().getStringExtra("type");
        long tempstartdatettime = getIntent().getLongExtra("startdatetime", 0);
        long tempenddatetime = getIntent().getLongExtra("enddatetime", 0);
        byte[] tempeventimage = getIntent().getByteArrayExtra("eventimage");
        int eventID = getIntent().getIntExtra("eventID", 0);
        int userID = getIntent().getIntExtra("userID",0);
        String userIDString = String.valueOf(userID);

        context = this;
        DB = new DBHelper(context);
        home = (ImageView) findViewById(R.id.Home);
        notification = (ImageView) findViewById(R.id.Notification);
        favourite = (ImageView) findViewById(R.id.Favourite);
        setting = (ImageView) findViewById(R.id.Setting);
        calendarbutton = (Button) findViewById(R.id.calendarbutton);
        overviewbutton = (Button) findViewById(R.id.overviewbutton);
        eventlist = (ListView) findViewById(R.id.eventlist);
        searchview = findViewById(R.id.searchview1);
        noevents = (TextView) findViewById(R.id.NoEvents);
        searchview.clearFocus();
        events = new ArrayList<>();
        events = DB.getAllFavourite(userID);

        EventsAdapter adapter = new EventsAdapter(context, R.layout.activity_home, events, DB);
        eventlist.setAdapter(adapter);

        if (adapter.isEmpty()) {
            // If adapter is empty, display "No Events"
            noevents.setVisibility(View.VISIBLE);
            eventlist.setVisibility(View.GONE); // Hide the ListView
        } else {
            // If adapter is not empty, hide "No Events" and show the ListView
            noevents.setVisibility(View.GONE);
            eventlist.setVisibility(View.VISIBLE);
        }

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return false;
            }
        });



        eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Event event = events.get(i);
                Intent intent = new Intent(context, ViewMoreFavourite.class);
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

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Notification.class);
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

        calendarbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MyCalendar.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        overviewbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Favourite.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }
    private void filterList(String newText) {
        List<Event> filteredList = new ArrayList<>();

        for (Event event: events) {
            if (event.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(event);
            }
        }

        EventsAdapter adapter = new EventsAdapter(context, R.layout.activity_home, filteredList, DB);


        if (filteredList.isEmpty()) {
            eventlist.setAdapter(adapter);
            noevents.setVisibility(View.VISIBLE);
            eventlist.setVisibility(View.GONE); // Hide the ListView
        } else {
            eventlist.setAdapter(adapter);
            noevents.setVisibility(View.GONE);
            eventlist.setVisibility(View.VISIBLE); // Hide the ListView
        }
    }
}



