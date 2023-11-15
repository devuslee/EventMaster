package com.example.eventmanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eventmaster.DBHelper;
import com.example.eventmaster.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class EventSetting extends AppCompatActivity {

    private Button editphone, logout, editpassword, createevent;
    private Context context;
    private DBHelper DB;
    private TextView username, email;
    private ImageView profileimage, home, notification, favourite, setting;
    private List<User> users;
    private ImageView eventimage;
    private Bitmap imageDB;
    private FirebaseAuth fAuth; // Firebase authentication instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_setting);

        context = this;
        DB = new DBHelper(context);
        fAuth = FirebaseAuth.getInstance();
        home = (ImageView) findViewById(R.id.Home);
        notification = (ImageView) findViewById(R.id.Notification);
        favourite = (ImageView) findViewById(R.id.Favourite);
        setting = (ImageView) findViewById(R.id.Setting);
        logout = (Button) findViewById(R.id.logout);
        editpassword = (Button) findViewById(R.id.editpassword);
        createevent = (Button) findViewById(R.id.CreateEvent);
        profileimage = (ImageView) findViewById(R.id.profileimage);
        username = (TextView) findViewById(R.id.username);
        email = (TextView) findViewById(R.id.email);
        editphone = (Button) findViewById(R.id.editphone);


        int userID = getIntent().getIntExtra("userID", 0);

        users = new ArrayList<>();
        users = DB.getUserInfo(userID);
        User user = users.get(0);
        String tempemail = user.getEmail();
        String tempusername = user.getUsername();

        String tempphone = user.getPhone();

        if ("NULL".equals(tempphone)) {
            editphone.setText("Add Phone Number");
        } else {
            editphone.setText("Edit Phone Number");
        }

        username.setText("Email: " + tempemail);
        email.setText("Username: " + tempusername);
        if (user.getProfileimage() != null && user.getProfileimage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getProfileimage(), 0, user.getProfileimage().length);
            int newWidth = 1250; // Desired width in pixels
            int newHeight = 1000; // Desired height in pixels

            // Get the bitmap from the ImageView
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            profileimage.setImageBitmap(scaledBitmap);


        } else {
            profileimage.setImageResource(R.drawable.noimage);
        }

        profileimage.setScaleType(ImageView.ScaleType.FIT_START);


        profileimage.getLayoutParams().width = 1000;
        profileimage.getLayoutParams().height = 1000;


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                1000, // Width
                700 // Height
        );

        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);


        int marginLeft = 225;
        layoutParams.setMargins(marginLeft, 40, 0, 0);


        profileimage.setLayoutParams(layoutParams);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int userID = getIntent().getIntExtra("userID", 0);
                Intent intent = new Intent(context, com.example.eventmanager.eventHome.class);
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
                Intent intent = new Intent(context, com.example.eventmanager.Favourite.class);
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

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut(); // Log out from Firebase
                Toast.makeText(EventSetting.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditPhone.class);
                int userID = getIntent().getIntExtra("userID", 0);
                String tempemail = DB.getUserEmail(userID);
                intent.putExtra("userEmail", tempemail);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        editpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EditProfile.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });

        createevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EventList.class);
                int userID = getIntent().getIntExtra("userID", 0);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    public void switchIntent(View objectView) {
        Intent intent = new Intent(this, UpdateProfile.class);
        int userID = getIntent().getIntExtra("userID", 0);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

}