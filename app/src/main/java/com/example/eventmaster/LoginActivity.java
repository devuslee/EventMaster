package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eventmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    Button btnlogin, btnregister, btnforget;

    Context context;
    DBHelper DB;
    int userID, userRole;
    ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        DB = new DBHelper(context);
        username = (EditText) findViewById(R.id.username1);
        password = (EditText) findViewById(R.id.password1);
        btnlogin = (Button) findViewById(R.id.btnsignin1);
        btnregister = (Button) findViewById(R.id.register);
        btnforget = (Button) findViewById(R.id.forgetaccount);
        DB = new DBHelper(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = username.getText().toString();
                String pass = password.getText().toString();
                userID = DB.getUserID(user, pass);
                userRole = DB.getUserRole(userID);

                if (user.equals("") || pass.equals("")) {
                    Toast.makeText(LoginActivity.this, "Fill in the blanks!", Toast.LENGTH_SHORT).show();
                } else {
                    if (userRole == 0) {
                        progressBar.setVisibility(View.VISIBLE);

                        // Attempt to login with Firebase
                        fAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = fAuth.getCurrentUser();
                                if (currentUser != null && currentUser.isEmailVerified()) {
                                    Toast.makeText(LoginActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), eventHome.class);
                                    userID = DB.getUserID(user, pass);
                                    intent.putExtra("userID", userID);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "Please verify your email to login.", Toast.LENGTH_SHORT).show();
                                    // Send verification email or handle unverified user
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Sign in successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), AdminHome.class);
                        userID = DB.getUserID(user, pass);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                    }
                }
            }
        });


        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

        btnforget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EmailOrPhone.class);
                startActivity(intent);
            }
        });
    }
}
