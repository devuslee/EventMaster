package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    Context context;
    DBHelper DB;
    Button btnverify, btnsendotp;
    int emailuserid, phoneuserid;
    EditText phoneNumber2, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        context = this;
        DB = new DBHelper(context);


        btnsendotp = (Button) findViewById(R.id.buttonSendEmailOtp);
        email = (EditText) findViewById(R.id.email123);




        btnsendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString().trim();
                emailuserid = DB.getUserIDByEmail(userEmail);

                FirebaseAuth fAuth = FirebaseAuth.getInstance();

                try {
                    if (userEmail.isEmpty()) {
                        // Display a message if the email field is empty
                        Toast.makeText(context, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    fAuth.sendPasswordResetEmail(userEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(context, "Password reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                                    // Password reset email sent successfully
                                } else {
                                    Toast.makeText(context, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                    // Handle the failure to send the password reset email
                                }
                            });
                } catch (Exception e) {
                    // Handle any potential exceptions, such as an empty user email
                    Toast.makeText(context, "An error occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace(); // Log the exception for debugging purposes
                }

                Intent intent = new Intent(context, ChangePassword.class);
                intent.putExtra("emailuserid", emailuserid);
                startActivity(intent);
            }
        });
    }
}