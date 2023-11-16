package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eventmaster.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Notification extends AppCompatActivity {

    private Context context;
    private DBHelper DB;

    ImageView backbutton, home,notification, setting, favourite;

    private TextView noevents;
    ListView notificationlist;
    private List<NotificationClass> notifications;

    private SearchView searchview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        context = this;
        DB = new DBHelper(context);

        searchview = findViewById(R.id.searchview1);
        notificationlist = (ListView) findViewById(R.id.notification);
        home = (ImageView) findViewById(R.id.Home);
        notification = (ImageView) findViewById(R.id.Notification);
        favourite = (ImageView) findViewById(R.id.Favourite);
        setting = (ImageView) findViewById(R.id.Setting);
        noevents = (TextView)findViewById(R.id.NoEvents);
        int userID = getIntent().getIntExtra("userID",0);
        notifications = new ArrayList<>();
        notifications = DB.getNotifications(userID);


        Collections.reverse(notifications);

        NotificationAdapter adapter = new NotificationAdapter(context, R.layout.notification_bar, notifications);
        notificationlist.setAdapter(adapter);

        if (adapter.isEmpty()) {
            // If adapter is empty, display "No Events"
            noevents.setVisibility(View.VISIBLE);
            notificationlist.setVisibility(View.GONE); // Hide the ListView
        } else {
            // If adapter is not empty, hide "No Events" and show the ListView
            noevents.setVisibility(View.GONE);
            notificationlist.setVisibility(View.VISIBLE);
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
                int userID = getIntent().getIntExtra("userID", 0);
                Intent intent = new Intent(context, Notification.class);
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

    private void filterList(String newText) {
        List<NotificationClass> filteredList = new ArrayList<>();


        for (NotificationClass notificationClass: notifications) {
            if (notificationClass.getEventname().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(notificationClass);
            }
        }

        NotificationAdapter adapter = new NotificationAdapter(context, R.layout.notification_bar, filteredList);


        if (filteredList.isEmpty()) {
            notificationlist.setAdapter(adapter);
            noevents.setVisibility(View.VISIBLE);
            notificationlist.setVisibility(View.GONE); // Hide the ListView
        } else {
            notificationlist.setAdapter(adapter);
            noevents.setVisibility(View.GONE);
            notificationlist.setVisibility(View.VISIBLE); // Hide the ListView
        }
    }
}