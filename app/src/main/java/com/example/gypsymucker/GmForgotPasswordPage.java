package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class GmForgotPasswordPage extends AppCompatActivity {

    EditText forgotEmail;
    Button forgotBtn;
    FirebaseAuth auth;
    long mLastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gm_forgot_password_page);

        forgotEmail = findViewById(R.id.forgotEmail);
        forgotBtn = findViewById(R.id.forgotBtn);
        auth = FirebaseAuth.getInstance();

        forgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String email = forgotEmail.getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(GmForgotPasswordPage.this, "Plz Enter Email", Toast.LENGTH_SHORT).show(); return;
                }else {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(GmForgotPasswordPage.this, "Reset Password link send Successfully, Go to Your email address to reset your password.", Toast.LENGTH_LONG).show();
                                forgotEmail.setText("");
                                startActivity(new Intent(GmForgotPasswordPage.this,GmMainLoginPage.class));
                            }else{
                                Toast.makeText(GmForgotPasswordPage.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();return;
                            }
                        }
                    });
                }


            }
        });


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }
}
