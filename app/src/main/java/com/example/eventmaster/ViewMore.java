package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmanager.FavouriteClass;
import com.example.eventmanager.R;
import com.example.eventmanager.eventHome;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ViewMore extends AppCompatActivity {

    private Context context;
    private DBHelper DB;

    private TextView username, location, description, type, startdatetime, enddatetime, enddatetime2;
    private ImageView eventimage, backbutton, launchbutton;
    private Button FavouriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_more);

        context = this;
        DB = new DBHelper(context);

        String tempname = getIntent().getStringExtra("name");
        String templocation = getIntent().getStringExtra("location");
        String tempdescription = getIntent().getStringExtra("description");
        String temptype = getIntent().getStringExtra("type");
        long tempstartdatettime = getIntent().getLongExtra("startdatetime", 0);
        long tempenddatetime = getIntent().getLongExtra("enddatetime", 0);
        byte[] tempeventimage = getIntent().getByteArrayExtra("eventimage");
        int userID = getIntent().getIntExtra("userID", 0);
        int eventID = getIntent().getIntExtra("eventID", 0);




        Bitmap bitmap = BitmapFactory.decodeByteArray(tempeventimage, 0, tempeventimage.length);

        launchbutton = (ImageView) findViewById(R.id.launchmap);
        username = (TextView) findViewById(R.id.username);
        location = (TextView) findViewById(R.id.Location);
        description = (TextView) findViewById(R.id.description);
        type = (TextView) findViewById(R.id.type);
        startdatetime = (TextView) findViewById(R.id.startdatetime);
        enddatetime = (TextView) findViewById(R.id.enddatetime);
        eventimage = (ImageView) findViewById(R.id.eventimage);
        FavouriteButton = (Button) findViewById(R.id.favouritebutton);
        backbutton = (ImageView) findViewById(R.id.backbutton);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date startDate = new Date(tempstartdatettime);
        Date endDate = new Date(tempenddatetime);

        String formattedStartDateTime = sdf.format(startDate);
        String formattedEndDateTime = sdf.format(endDate);

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("d 'of' MMMM, yyyy", Locale.ENGLISH);
        SimpleDateFormat displayTimeFormat = new SimpleDateFormat("h:mma", Locale.ENGLISH);

        String formattedStartDateDisplay = displayDateFormat.format(startDate);
        String formattedStartTimeDisplay = displayTimeFormat.format(startDate);
        String formattedEndDateDisplay = displayDateFormat.format(endDate);
        String formattedEndTimeDisplay = displayTimeFormat.format(endDate);

        enddatetime2 = (TextView) findViewById(R.id.enddatetime2);
        String locationName = templocation;
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            Address address = addresses.get(0);

            // Extract all available address components
            String addressLine = address.getAddressLine(0); // You can use getAddressLine(1), getAddressLine(2), etc., for additional lines

            // Format the address information as needed
            String formattedAddress = "Location: " + addressLine;

            location.setText(formattedAddress);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error while validating location.", Toast.LENGTH_SHORT).show();
        }
        username.setText(tempname);

        description.setText(tempdescription);
        type.setText(temptype );
        startdatetime.setText(formattedStartDateDisplay );
        enddatetime.setText(formattedStartTimeDisplay );
        enddatetime2.setText(formattedEndTimeDisplay );
        eventimage.setImageBitmap(bitmap);

        eventimage.getLayoutParams().width = 1000;
        eventimage.getLayoutParams().height = 500;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) eventimage.getLayoutParams();

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        layoutParams.setMargins(0, 20, 0, 0);

        eventimage.setLayoutParams(layoutParams);

        launchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String locationName = templocation;
                Geocoder geocoder = new Geocoder(context);
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
        FavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String currentDateTime = sdf.format(new Date());
                FavouriteClass favouriteclass = new FavouriteClass(0, userID, eventID);
                NotificationClass notification = new NotificationClass(0, eventID, userID, "Your event has been added to the calendar", "Add", currentDateTime, tempname);
                DB.addUpdateNotification(notification);
                DB.addFavourite(favouriteclass);

                Intent intent = new Intent(context, eventHome.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("eventID", eventID);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}