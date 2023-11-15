package com.example.eventmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.eventmanager.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EditProfile extends AppCompatActivity {

    DBHelper DB;
    Context context;

    ImageView backbutton;
    EditText password, repassword, oldpassword;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        context = this;
        DB = new DBHelper(context);

        oldpassword = (EditText) findViewById(R.id.oldpassword);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);
        updateButton = (Button) findViewById(R.id.updatepassword);
        backbutton = (ImageView) findViewById(R.id.backbutton);
        int userID = getIntent().getIntExtra("userID", 0);
        String tempoldpass = DB.getOldPassword(userID);
        String tempemail = DB.getUserEmail(userID);


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldpass = oldpassword.getText().toString();
                String pass = password.getText().toString();
                String repass = repassword.getText().toString();

                if (oldpass.equals("") || pass.equals("") || repass.equals(""))
                    Toast.makeText(context, "Fill in the blanks!", Toast.LENGTH_SHORT).show();
                else {
                    if (tempoldpass.equals(oldpass)) {
                        if (pass.equals(repass)) {
                            DB.updateUserPassword(userID, pass);
                            Toast.makeText(context, "Password udpated!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, EventSetting.class);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Re-typed password not the same!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Wrong old password", Toast.LENGTH_SHORT).show();
                    }
                }

                auth.signInWithEmailAndPassword(tempemail, tempoldpass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                user.updatePassword(pass)
                                        .addOnCompleteListener(passwordUpdateTask -> {
                                            if (passwordUpdateTask.isSuccessful()) {
                                                // Password updated successfully
                                                Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show();
                                                // Proceed with other actions or navigate to a new activity
                                            } else {
                                                // Password update failed
                                                Toast.makeText(context, "Password update failed", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                // Sign-in failed, display an error message
                                Toast.makeText(context, "Sign-in failed. Incorrect current password.", Toast.LENGTH_SHORT).show();
                            }
                        });
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