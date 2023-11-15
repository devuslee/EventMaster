package com.example.eventmaster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.example.eventmanager.R;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.hbb20.CountryCodePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.hbb20.CountryCodePicker;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class ForgetPasswordPhone extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "OTPVerification";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String verificationId;
    private EditText phoneNumberEditText; // Ensure this is only initialized once
    String userEmail;
    Context context;
    DBHelper DB;
    private Button skip;
    Long timeoutSeconds = 120L;
    TextView resendOtpTextView;

    private CountryCodePicker ccp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_phone);

        context = this;
        DB = new DBHelper(context);
        mAuth = FirebaseAuth.getInstance();


        Button buttonVerifyOtp = findViewById(R.id.buttonVerifyOtp);
        phoneNumberEditText = findViewById(R.id.phoneNumber);
        userEmail = getIntent().getStringExtra("email");
        resendOtpTextView = findViewById(R.id.resend_otp_textview); // Assuming you have a TextView with this ID in your XML layout
        Button buttonSendOtp = findViewById(R.id.buttonSendOtp);
        ccp = findViewById(R.id.countryCodePicker);
        ccp.registerCarrierNumberEditText(phoneNumberEditText);



        buttonVerifyOtp.setOnClickListener(v -> verifyOtp());

        resendOtpTextView.setVisibility(View.GONE); // Initially set the TextView as GONE


        resendOtpTextView.setOnClickListener((v) -> {
            String fullPhoneNumber = ccp.getFullNumberWithPlus();
            sendOtp(fullPhoneNumber, true);
        });


        buttonSendOtp.setOnClickListener(v -> {

            String fullPhoneNumber = ccp.getFullNumberWithPlus();
            sendOtp(fullPhoneNumber, false);

            // Disable the button after clicking
            buttonSendOtp.setEnabled(false);
            buttonSendOtp.setAlpha(0.5f); // Adjust the button appearance to indicate it's disabled

            startResendTimer();
        });

    }

    private void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        String fullPhoneNumber = ccp.getFullNumberWithPlus();

        // Ensure phoneNumberEditText is not null
        if (phoneNumberEditText == null) {
            Toast.makeText(context, "Phone number field is not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Full Phone Number: " + phoneNumber, Toast.LENGTH_SHORT).show();

        if (phoneNumber.isEmpty()) {
            Toast.makeText(context, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthOptions.Builder optionsBuilder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks);

        if (isResend && mResendToken != null) {
            optionsBuilder.setForceResendingToken(mResendToken);
        }

        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build());
    }




    void startResendTimer() {
        resendOtpTextView.setVisibility(View.VISIBLE);
        Button buttonSendOtp = findViewById(R.id.buttonSendOtp);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--; // Decrement by 1 second
                runOnUiThread(() -> {
                    resendOtpTextView.setText("Resend OTP in " + (timeoutSeconds / 2) + " seconds");
                });
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 120L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtpTextView.setEnabled(true);
                        resendOtpTextView.setVisibility(View.GONE);
                        buttonSendOtp.setEnabled(true); // Re-enable the button
                        buttonSendOtp.setAlpha(1.0f); // Reset the button appearance
                    });
                }
            }
        }, 0, 1000);
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            Log.d(TAG, "onVerificationCompleted:" + credential);
            signInWithPhoneAuthCredential(credential);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.w(TAG, "onVerificationFailed", e);

            // Provide a more detailed error log
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(context, "Invalid request. Please ensure your phone number is entered correctly.", Toast.LENGTH_LONG).show();
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Toast.makeText(context, "We have detected too many requests from your device. Please try again later.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Error during verification: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d(TAG, "onCodeSent:" + s);
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            Toast.makeText(context, "OTP sent", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyOtp() {
        String otp = ((EditText) findViewById(R.id.editTextCode)).getText().toString();
        if (verificationId == null || otp.isEmpty()) {
            Toast.makeText(context, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        linkPhoneNumberWithCurrentUser(credential);
    }

    private void linkPhoneNumberWithCurrentUser(PhoneAuthCredential credential) {
        String phonenumber = phoneNumberEditText.getText().toString();
        int userid = DB.getUserIDByPhone(phonenumber);
        String email = DB.getUserEmail(userid);
        String password = DB.getOldPassword(userid);
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, signInTask -> {
                        if (signInTask.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            currentUser.linkWithCredential(credential)
                                    .addOnCompleteListener(this, linkTask -> {

                                            Toast.makeText(context, "Verification complete!", Toast.LENGTH_SHORT).show();

                                            // Retrieve the phone number from the EditText
                                            String phoneNumber = ((EditText) findViewById(R.id.phoneNumber)).getText().toString();

                                            int userID = DB.getUserIDByPhone(phoneNumber);
                                            userEmail = DB.getUserEmail(userID);
                                            // Update the local database with the phone number
                                            DB.updateUserPhoneNumber(userEmail, phoneNumber);

                                            Toast.makeText(context, "" + userID, Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(context, ChangePassword.class);
                                            intent.putExtra("emailuserid", userID);
                                            startActivity(intent);

                                    });
                        } else {
                            // Sign-in failed, show a message
                            Toast.makeText(context, "Authentication failed: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(context, "Phone number does not exist!", Toast.LENGTH_SHORT).show();
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential: success");
                        FirebaseUser user = task.getResult().getUser();
                        // Update UI - Inform the user they have successfully signed in
                        Toast.makeText(context, "Successfully signed in with phone number.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w(TAG, "signInWithCredential: failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(context, "The verification code entered was invalid.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error signing in with phone auth credential: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



}
