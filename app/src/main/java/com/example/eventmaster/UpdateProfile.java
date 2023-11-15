package com.example.eventmaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.eventmanager.EventSetting;
import com.example.eventmanager.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateProfile extends AppCompatActivity {

    private Button Addbutton;
    private ImageView eventimage, profileimage;
    private Bitmap imageDB;
    private Uri imageFilePath;

    private Context context;

    private List<User> users;
    private DBHelper DB;

    private static final int PICK_IMAGE_REQUEST=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        context = this;
        DB = new DBHelper(context);

        int userID = getIntent().getIntExtra("userID", 0);

        users = new ArrayList<>();
        users = DB.getUserInfo(userID);
        User user = users.get(0);

        eventimage = (ImageView) findViewById(R.id.eventimage);
        Addbutton = (Button) findViewById(R.id.AddButton);

        if (user.getProfileimage() != null && user.getProfileimage().length > 0) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(user.getProfileimage(), 0, user.getProfileimage().length);
            int newWidth = 1250; // Desired width in pixels
            int newHeight = 1000; // Desired height in pixels

            // Get the bitmap from the ImageView
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            eventimage.setImageBitmap(scaledBitmap);


        } else {
            eventimage.setImageResource(R.drawable.noimage);
        }

        Addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable drawable = (BitmapDrawable) eventimage.getDrawable();
                imageDB = drawable.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageDB.compress(Bitmap.CompressFormat.PNG, PICK_IMAGE_REQUEST, stream);
                byte[] byteArray = stream.toByteArray();

                int userID = getIntent().getIntExtra("userID", 0);

                DB.updateProfileImage(userID, byteArray);

                Intent intent = new Intent(context, EventSetting.class);
                intent.putExtra("userID", userID);
                Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        });



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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                // Get the selected image URI
                Uri selectedImage = data.getData();

                // Decode the image into a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                // Adjust the size of the image
                int newWidth = 1250; // Desired width in pixels
                int newHeight = 1000; // Desired height in pixels
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

                // Set the adjusted image to the ImageView (eventimage)
                eventimage.setImageBitmap(scaledBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}