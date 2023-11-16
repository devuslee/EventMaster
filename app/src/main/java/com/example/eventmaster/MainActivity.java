package com.example.eventmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eventmaster.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Context context;
    EditText email,password,repassword, username;
    Button btnsignup, btnsignin;
    DBHelper DB;
    int userID;
    ProgressBar progressBar;

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = this;
        DB = new DBHelper(context);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        username = (EditText) findViewById(R.id.username);
        btnsignup = (Button) findViewById(R.id.btnsignup);
        btnsignin = (Button) findViewById(R.id.btnsignin);
        DB = new DBHelper(this);
        progressBar = findViewById(R.id.progressBar);
        fAuth= FirebaseAuth.getInstance();

        btnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email1 = email.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();
                String username1 = username.getText().toString();

                if(email1.equals("") || pass.equals("") || repass.equals("")) {
                    Toast.makeText(MainActivity.this, "Fill in the blanks!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!pass.equals(repass)) {
                    Toast.makeText(MainActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Boolean checkemail = DB.checkemail(email1);
                if (checkemail) {
                    Toast.makeText(MainActivity.this, "User already exists! Please sign in!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                // Register user in Firebase
                fAuth.createUserWithEmailAndPassword(email1, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // New addition: Send Verification Email
                            FirebaseUser user = fAuth.getCurrentUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(MainActivity.this, "Verification email sent! Please check your inbox.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(MainActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            // User registered in Firebase, now register in local database
                            byte[] imageBytes = null;
                            User user1 = new User(email1, pass, "NULL", username1, imageBytes , 0);
                            Boolean insert = DB.addUser(user1);
                            if (insert) {
                                Toast.makeText(MainActivity.this, "User registered! Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
                                // I recommend you redirect them to the login page here, not the OTP page.
                                // Because they should first verify their email.
                                Intent intent = new Intent(context, otpverification.class);
                                intent.putExtra("email", email1);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed registration in local database!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });


        btnsignin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
               startActivity(intent);
           }
        });

        final EditText passwordEditText = findViewById(R.id.password);
        final ImageView togglePasswordVisibilityImageView = findViewById(R.id.togglePasswordVisibility);
        final Typeface originalTypeface = passwordEditText.getTypeface();

        togglePasswordVisibilityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    togglePasswordVisibilityImageView.setImageResource(R.drawable.eye_on);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    togglePasswordVisibilityImageView.setImageResource(R.drawable.eye_off);
                }
                passwordEditText.setTypeface(originalTypeface); // Re-apply the original typeface
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        // Repeat the same steps for the confirm password EditText and ImageView
        final EditText confirmPasswordEditText = findViewById(R.id.repassword);
        final ImageView toggleConfirmPasswordVisibilityImageView = findViewById(R.id.toggleConfirmPasswordVisibility);
        final Typeface originalConfirmTypeface = confirmPasswordEditText.getTypeface();

        toggleConfirmPasswordVisibilityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirmPasswordEditText.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    toggleConfirmPasswordVisibilityImageView.setImageResource(R.drawable.eye_on);
                } else {
                    confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    toggleConfirmPasswordVisibilityImageView.setImageResource(R.drawable.eye_off);
                }
                confirmPasswordEditText.setTypeface(originalConfirmTypeface); // Re-apply the original typeface
                confirmPasswordEditText.setSelection(confirmPasswordEditText.getText().length());
            }
        });
    }

}