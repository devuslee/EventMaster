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

import com.example.eventmanager.R;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UpdateEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Context context;
    private DBHelper DB;

    private String text, starttime, endtime, date;
    private EditText EventName, EventLocation, EventDescription;
    private Button UpdateButton, datebutton, timebutton, endtimebutton, launchbutton;
    Spinner spinner;
    private TextView datetext, starttimetext, endtimetext;
    private long startTimeMillis, endTimeMillis;

    private ImageView eventimage, backbutton;
    private Bitmap imageDB;
    private Uri imageFilePath;


    private static final int PICK_IMAGE_REQUEST=100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        context = this;
        DB = new DBHelper(context);

        String tempname = getIntent().getStringExtra("name");
        String templocation = getIntent().getStringExtra("location");
        String tempdescription = getIntent().getStringExtra("description");
        String temptype = getIntent().getStringExtra("type");
        long tempstartdatettime = getIntent().getLongExtra("startdatetime",0);
        long tempenddatetime = getIntent().getLongExtra("enddatetime",0);
        byte[] tempeventimage = getIntent().getByteArrayExtra("eventimage");


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        UpdateButton = (Button) findViewById(R.id.UpdateButton);
        datebutton = (Button) findViewById(R.id.datebutton);
        timebutton = (Button) findViewById(R.id.timebutton);
        endtimebutton = (Button) findViewById(R.id.endtimebutton);
        eventimage = (ImageView) findViewById(R.id.eventimage);
        backbutton = (ImageView) findViewById(R.id.backbutton);
        launchbutton = (Button) findViewById(R.id.launchmap);

        Date startTime = new Date(tempstartdatettime);
        Date endTime = new Date(tempenddatetime);

        String formattedStartTime = sdf.format(startTime);
        String formattedEndTime = sdf.format(endTime);

        String[] startparts = formattedStartTime.split(" ");
        String[] endparts = formattedEndTime.split(" ");

        Bitmap bitmap = BitmapFactory.decodeByteArray(tempeventimage, 0, tempeventimage.length);


        date = startparts[0];
        starttime = startparts[1];
        endtime = endparts[1];



        EventName = (EditText) findViewById(R.id.eventname);
        EventLocation = (EditText) findViewById(R.id.location);
        EventDescription= (EditText) findViewById(R.id.description);
        spinner = findViewById(R.id.spinner1);
        datetext = (TextView) findViewById(R.id.date);
        starttimetext = (TextView) findViewById(R.id.time);
        endtimetext = (TextView) findViewById(R.id.endtime);

        EventName.setText(tempname);
        EventLocation.setText(templocation);
        EventDescription.setText(tempdescription);
        spinner = findViewById(R.id.spinner1);
        datetext.setText(date);
        starttimetext.setText(starttime);
        endtimetext.setText(endtime);
        eventimage.setImageBitmap(bitmap);

        eventimage.setScaleType(ImageView.ScaleType.FIT_START);
        int desiredWidth = 500;
        int desiredHeight = 350;
        eventimage.getLayoutParams().width = desiredWidth;
        eventimage.getLayoutParams().height = desiredHeight;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(desiredWidth, desiredHeight);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;



        eventimage.setLayoutParams(layoutParams);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int defaultSelection = adapter.getPosition(temptype);
        spinner.setSelection(defaultSelection);

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

        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = EventName.getText().toString();
                String location = EventLocation.getText().toString();
                String description = EventDescription.getText().toString();
                String type = text;

                if (name.isEmpty() || location.isEmpty() || description.isEmpty() || date == null || starttime == null || endtime == null) {
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

                    BitmapDrawable drawable = (BitmapDrawable) eventimage.getDrawable();
                    imageDB = drawable.getBitmap();

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    imageDB.compress(Bitmap.CompressFormat.PNG, PICK_IMAGE_REQUEST, stream);
                    byte[] byteArray = stream.toByteArray();

                    Geocoder geocoder = new Geocoder(UpdateEvent.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(location, 1);

                        if (addresses != null && addresses.size() > 0) {
                            Address address = addresses.get(0);
                            Log.i("tag", "Location exists: " + address.getFeatureName());

                            int userID = getIntent().getIntExtra("userID", 0);
                            int eventID = getIntent().getIntExtra("eventID", 0);
                            int status = getIntent().getIntExtra("status", 0);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            String currentDateTime = sdf.format(new Date());

                            Event event = new Event(eventID, endTimeMillis, startTimeMillis, location, description, type, name, userID, byteArray, status);
                            NotificationClass notification = new NotificationClass(0, eventID,userID, "Your event has been updated", "Update", currentDateTime, name);
                            DB.addUpdateNotification(notification);
                            DB.updateEvent(event, userID);

                            ArrayList<Integer> userIDsArray = DB.findAllFavouriteUsers(eventID);

                            for (int i = 0; i < userIDsArray.size(); i++) {
                                NotificationClass notificationforall = new NotificationClass(0, eventID, userIDsArray.get(i), "Your event has been updated", "Update", currentDateTime, name);
                                DB.addUpdateNotification(notificationforall);
                            }

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
        });

        launchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationName = EventLocation.getText().toString();
                Geocoder geocoder = new Geocoder(UpdateEvent.this);
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

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

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
                date = String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
                datetext.setText(String.valueOf(year) + "." + String.valueOf(month) + "." + String.valueOf(day));
            }
        }, 2023, 10, 10);

        dialog.show();
    }

    private void openStartTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                starttime = String.valueOf(hour) + ":" + String.format("%02d", minute);
                starttimetext.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
            }
        }, 6, 44,true);

        dialog.show();
    }

    private void openEndTimeDialog() {
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minute) {
                endtime = String.valueOf(hour) + ":" + String.format("%02d", minute);
                endtimetext.setText(String.valueOf(hour) + ":" + String.valueOf(minute));
            }
        }, 6, 44,true);

        dialog.show();
    }

    public void chooseImage(View objectView) {
        try
        {
            Intent objectIntent=new Intent();
            objectIntent.setType("image/*");

            objectIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(objectIntent, PICK_IMAGE_REQUEST);
        }
        catch (Exception e)
        {
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

                int desiredWidth = 1000;
                int desiredHeight = 700;
                eventimage.getLayoutParams().width = desiredWidth;
                eventimage.getLayoutParams().height = desiredHeight;

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(desiredWidth, desiredHeight);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;


                int marginLeft = 350;
                layoutParams.setMargins(marginLeft, 10, 0, 0);

                eventimage.setLayoutParams(layoutParams);

                eventimage.setImageBitmap(imageDB);

            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
