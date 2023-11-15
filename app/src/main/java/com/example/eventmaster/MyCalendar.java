package com.example.eventmaster;

import androidx.annotation.NonNull;
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
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MyCalendar extends AppCompatActivity {

    CalendarView calendarView;
    Calendar calendar;
    private Button calendarbutton, overviewbutton;

    private ImageView home, notification, favourite, setting;
    private Context context;
    private DBHelper DB;

    private ListView eventlist;
    private List<Event> events;

    Date currentDate = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    String formatteddate = sdf.format(currentDate);

    String[] dateTimeComponents = formatteddate.split(" ");
    String date1 = dateTimeComponents[0];
    String time = dateTimeComponents[1];

    String[] datePart = date1.split("-");
    String[] timePart = time.split(":");

    int year = Integer.parseInt(datePart[0]);
    int month = Integer.parseInt(datePart[1]);
    int day = Integer.parseInt(datePart[2]);

    int hour = Integer.parseInt(timePart[0]);
    int minute = Integer.parseInt(timePart[1]);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        context = this;
        DB = new DBHelper(context);

        calendarView = (CalendarView) findViewById(R.id.calenderview);
        calendar = Calendar.getInstance();


        home = (ImageView) findViewById(R.id.Home);
        notification = (ImageView) findViewById(R.id.Notification);
        favourite = (ImageView) findViewById(R.id.Favourite);
        setting = (ImageView) findViewById(R.id.Setting);
        eventlist = (ListView) findViewById(R.id.eventlist);
        calendarbutton = (Button) findViewById(R.id.calendarbutton);
        overviewbutton = (Button) findViewById(R.id.overviewbutton);
        int userID = getIntent().getIntExtra("userID", 0);

        events = new ArrayList<>();
        events = DB.getAllFavourite(userID);

        EventsAdapter adapter = new EventsAdapter(context, R.layout.activity_home, events, DB);
        eventlist.setAdapter(adapter);

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

        setDate(day,month,year);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                long selectedDateInMillis = calendar.getTimeInMillis();

                boolean hasEvent = hasEventOnDate(selectedDateInMillis);

                // Highlight the selected date if there is an event
                if (hasEvent) {
                    calendarView.setDate(selectedDateInMillis, true, true);
                } else {
                    // Reset the background color to the default if there is no event
                    calendarView.setDate(selectedDateInMillis, false, true);
                }

                Toast.makeText(MyCalendar.this, "Selected date: " + year + "/" + (month + 1) + "/" + day, Toast.LENGTH_SHORT).show();
                events = DB.getDateEvent(userID, selectedDateInMillis);

                EventsAdapter adapter = new EventsAdapter(context, R.layout.activity_home, events, DB);
                eventlist.setAdapter(adapter);
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

    public void setDate(int day, int month, int year){
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long milli = calendar.getTimeInMillis();
        calendarView.setDate(milli);
    }

    public boolean hasEventOnDate(long dayInMillis) {
        for (Event event : events) {
            long eventStartTime = event.getStartdatetime();
            long eventEndTime = event.getEnddatetime();

            // Check if the event falls within the specified day
            if (eventStartTime >= dayInMillis && eventEndTime <= (dayInMillis + 86400000)) {
                return true; // Event found on the specified day
            }
        }

        return false; // No events found on the specified day
    }



}
