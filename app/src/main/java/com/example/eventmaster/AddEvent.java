package com.example.eventmaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Blob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.icu.util.Calendar;

import com.example.eventmanager.R;

public class AddEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context context;
    private DBHelper DB;

    private String text, starttime, endtime, date;
    private EditText EventName, EventLocation, EventDescription;
    private Button Addbutton, datebutton, timebutton, endtimebutton, launchbutton;
    Spinner spinner;
    private TextView datetext, timetext, endtimetext;
    private long startTimeMillis, endTimeMillis;
    private ImageView eventimage, backbutton;
    private Bitmap imageDB;
    private Uri imageFilePath;


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


    private static final int PICK_IMAGE_REQUEST = 100;


    String[] types = {"Select Option", "Sport", "Games", "Movies", "Others"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);


        context = this;
        DB = new DBHelper(context);

        backbutton = (ImageView) findViewById(R.id.backbutton);

        datetext = (TextView) findViewById(R.id.date);
        datebutton = (Button) findViewById(R.id.datebutton);

        timetext = (TextView) findViewById(R.id.time);
        timebutton = (Button) findViewById(R.id.timebutton);

        endtimetext = (TextView) findViewById(R.id.endtime);
        endtimebutton = (Button) findViewById(R.id.endtimebutton);

        EventName = (EditText) findViewById(R.id.eventname);
        EventLocation = (EditText) findViewById(R.id.location);
        EventDescription = (EditText) findViewById(R.id.description);

        launchbutton = (Button) findViewById(R.id.launchmap);

        Addbutton = (Button) findViewById(R.id.AddButton);
        spinner = findViewById(R.id.spinner1);

        eventimage = (ImageView) findViewById(R.id.eventimage);

        HintAdapter adapter = new HintAdapter(context, android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setSelection(0, false); // Set the initial text without triggering the selection
        spinner.setOnItemSelectedListener(this);


        datebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog();
            }
        });

        timebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStartTimeDialog();
            }
        });

        endtimebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEndTimeDialog();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = EventName.getText().toString();
                String location = EventLocation.getText().toString();
                String description = EventDescription.getText().toString();
                String type = text;




                if (name.isEmpty() || location.isEmpty() || description.isEmpty() || date == null || starttime == null || endtime == null || type == null) {
                    Toast.makeText(context, "Please fill in all the required fields", Toast.LENGTH_SHORT).show();
                } else {
                    String startTimeString = date + " " + starttime;
                    String endTimeString = date + " " + endtime;

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        Date startTime = sdf.parse(startTimeString);
                        Date endTime = sdf.parse(endTimeString);

                        startTimeMillis = startTime.getTime();
                        endTimeMillis = endTime.getTime();


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long currentTime = System.currentTimeMillis();
                    long timeAfter24Hours = currentTime + (24 * 60 * 60 * 1000); // 24 hours in milliseconds
                    if (startTimeMillis < timeAfter24Hours || endTimeMillis < timeAfter24Hours) {
                        Toast.makeText(context, "The time is too close to current time!", Toast.LENGTH_SHORT).show();
                    } else {

                        BitmapDrawable drawable = (BitmapDrawable) eventimage.getDrawable();
                        imageDB = drawable.getBitmap();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        imageDB.compress(Bitmap.CompressFormat.PNG, PICK_IMAGE_REQUEST, stream);
                        byte[] byteArray = stream.toByteArray();

                        Geocoder geocoder = new Geocoder(AddEvent.this);
                        try {
                            List<Address> addresses = geocoder.getFromLocationName(location, 1);

                            if (addresses != null && addresses.size() > 0) {
                                Address address = addresses.get(0);
                                Log.i("tag", "Location exists: " + address.getFeatureName());

                                int userID = getIntent().getIntExtra("userID", 0);


                                //0 is pending, 1 is approved and 2 is rejected
                                Event event = new Event(0, endTimeMillis, startTimeMillis, location, description, type, name, userID, byteArray, 0);
                                DB.addEvent(event, userID);
                                Intent intent = new Intent(context, EventList.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);

                            } else {
                                Toast.makeText(context, "Location does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Error while validating location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                Toast.makeText(context, "Your request has been sent to HR", Toast.LENGTH_SHORT).show();
            }
        });

        launchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationName = EventLocation.getText().toString();
                Geocoder geocoder = new Geocoder(AddEvent.this);
                try {
                    List<Address> addresses = geocoder.getFromLocationName(locationName, 1);

                    if (addresses != null && addresses.size() > 0) {
                        Address address = addresses.get(0);
                        Log.i("tag", "Location exists: " + address.getFeatureName());
                        double doubleLat = address.getLatitude();
                        double doubleLong = address.getLongitude();
                        Log.i("tag", doubleLat + "," + doubleLong);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.parse("geo:" + doubleLat + "," + doubleLong + "?q=" + Uri.encode(locationName));
                        intent.setData(uri);
                        Intent chooser = Intent.createChooser(intent, "Launch Maps");
                        startActivity(chooser);
                    } else {
                        Toast.makeText(context, "Location does not exist.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error while validating location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void openDateDialog() {

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                int fixedmonth = month + 1;
                date = String.valueOf(year) + "-" + String.format("%02d", fixedmonth) + "-" + String.format("%02d", day);
                datetext.setText(String.valueOf(year) + "." + String.valueOf(fixedmonth) + "." + String.valueOf(day));
            }
        }, year, month - 1, day);

        dialog.show();
    }

    private void openStartTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                starttime = String.valueOf(hour) + ":" + String.format("%02d", minute);
                timetext.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
            }
        }, hour, minute, true);

        dialog.show();
    }

    private void openEndTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                endtime = String.valueOf(hour) + ":" + String.format("%02d", minute);
                endtimetext.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
            }
        }, hour, minute, true);

        dialog.show();
    }

    public void chooseImage(View objectView) {
        try {
            Intent objectIntent = new Intent();
            objectIntent.setType("image/*");

            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent, PICK_IMAGE_REQUEST);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageFilePath = data.getData();
                imageDB = MediaStore.Images.Media.getBitmap(getContentResolver(), imageFilePath);

                eventimage.setScaleType(ImageView.ScaleType.FIT_START);
                int desiredWidth = 500;
                int desiredHeight = 350;
                eventimage.getLayoutParams().width = desiredWidth;
                eventimage.getLayoutParams().height = desiredHeight;

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(desiredWidth, desiredHeight);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;


                int marginLeft = 125;
                layoutParams.setMargins(marginLeft, 0, 0, 0);

                eventimage.setLayoutParams(layoutParams);

                eventimage.setImageBitmap(imageDB);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}