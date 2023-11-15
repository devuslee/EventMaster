package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmanager.R;

import java.util.ArrayList;
import java.util.List;

public class AdminHome extends AppCompatActivity {

    private Context context;
    private DBHelper DB;

    private ListView eventlist;
    private TextView countevents, noevents;
    private Button add;

    private ImageView logout;
    private List<Event> events;
    private EditText searchtext;

    private SearchView searchview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        context = this;
        DB = new DBHelper(context);
        searchview = findViewById(R.id.searchview1);
        eventlist = (ListView) findViewById(R.id.eventlist);
        noevents = (TextView) findViewById(R.id.NoEvents);
        logout = (ImageView) findViewById(R.id.logout);

        int userID = getIntent().getIntExtra("userID", 0);
        String userIDString = String.valueOf(userID);


        events = new ArrayList<>();
        events = DB.getAllEventsPending(userIDString);

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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        eventlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Event event = events.get(i);
                Intent intent = new Intent(context, ViewMoreAdmin.class);
                intent.putExtra("name", event.getName());
                intent.putExtra("location", event.getLocation());
                intent.putExtra("description", event.getDescription());
                intent.putExtra("type", event.getType());
                intent.putExtra("startdatetime", event.getStartdatetime());
                intent.putExtra("enddatetime", event.getEnddatetime());
                intent.putExtra("eventimage", event.getImage());
                intent.putExtra("eventID", event.getEventID());
                intent.putExtra("userID", userID);
                startActivity(intent);

            }
        });
    }

    private void filterList(String newText) {
        List<Event> filteredList = new ArrayList<>();
        EventsAdapter adapter = new EventsAdapter(context, R.layout.activity_home, filteredList, DB);

        for (Event event: events) {
            if (event.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(event);
            }
        }

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
