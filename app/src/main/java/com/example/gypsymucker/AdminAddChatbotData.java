package com.example.gypsymucker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AdminAddChatbotData extends AppCompatActivity {

    com.google.android.material.textfield.TextInputLayout editText10, editText1, editText2, editText3, editText4, editText5, editText6, editText7, editText8, editText9;
    EditText answerEditText, editTextQuestion, editText100, editText11, editText22, editText33, editText44, editText55, editText66, editText77, editText88, editText99;
    ImageButton add,sub;
    Button addData;
    String ansStep1 = "",ansStep2= "",ansStep3= "",ansStep4= "",ansStep5= "",ansStep6= "",ansStep7= "",ansStep8= "",ansStep9= "",ansStep10= "";
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_chatbot_data);


        add = findViewById(R.id.add);
        sub = findViewById(R.id.sub);
        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        editText4 = findViewById(R.id.editText4);
        editText5 = findViewById(R.id.editText5);
        editText6 = findViewById(R.id.editText6);
        editText7 = findViewById(R.id.editText7);
        editText8 = findViewById(R.id.editText8);
        editText9 = findViewById(R.id.editText9);
        editText10 = findViewById(R.id.editText10);

        editTextQuestion = findViewById(R.id.editTextQuestion);
        answerEditText = findViewById(R.id.answerEditText);
        editText11 = findViewById(R.id.editText11);
        editText22 = findViewById(R.id.editText22);
        editText33 = findViewById(R.id.editText33);
        editText44 = findViewById(R.id.editText44);
        editText55 = findViewById(R.id.editText55);
        editText66 = findViewById(R.id.editText66);
        editText77 = findViewById(R.id.editText77);
        editText88 = findViewById(R.id.editText88);
        editText99 = findViewById(R.id.editText99);
        editText100 = findViewById(R.id.editText100);

        addData = findViewById(R.id.addData);

        databaseReference = FirebaseDatabase.getInstance().getReference("ChatBot");

        final int[] i = {0} ,j = {0};


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(i[0] ==10) {
                    Toast.makeText(AdminAddChatbotData.this, "Chief 10 step completed! \uD83D\uDE44 ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (i[0] ==0){
                    editText1.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 1 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==1){
                    editText2.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 2 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==2){
                    editText3.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 3 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==3){
                    editText4.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 4 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==4){
                    editText5.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 5 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==5){
                    editText6.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 6 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==6){
                    editText7.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 7 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==7){
                    editText8.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 8 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==8){
                    editText9.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 9 Added!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==9){
                    editText10.setVisibility(VISIBLE);
                    Toast.makeText(AdminAddChatbotData.this, "Step 10 Added!", Toast.LENGTH_SHORT).show();
                }

                i[0] = i[0] + 1;

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                j[0] = i[0];


                if(i[0] ==0) {
                    Toast.makeText(AdminAddChatbotData.this, "Chief you must add first! \uD83E\uDD26\u200D♂", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (j[0] == 1){
                    editText1.setVisibility(GONE);
                    editText11.setText("");
                    ansStep1 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 1 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==2){
                    editText2.setVisibility(GONE);
                    editText22.setText("");
                    ansStep2 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 2 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==3){
                    editText3.setVisibility(GONE);
                    editText33.setText("");
                    ansStep3 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 3 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==4){
                    editText4.setVisibility(GONE);
                    editText44.setText("");
                    ansStep4 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 4 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==5){
                    editText5.setVisibility(GONE);
                    editText55.setText("");
                    ansStep5 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 5 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==6){
                    editText6.setVisibility(GONE);
                    editText66.setText("");
                    ansStep6 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 6 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==7){
                    editText7.setVisibility(GONE);
                    editText77.setText("");
                    ansStep7 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 7 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==8){
                    editText8.setVisibility(GONE);
                    editText88.setText("");
                    ansStep8 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 8 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==9){
                    editText9.setVisibility(GONE);
                    editText99.setText("");
                    ansStep9 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 9 Removed!", Toast.LENGTH_SHORT).show();
                }

                if (i[0] ==10){
                    editText10.setVisibility(GONE);
                    editText100.setText("");
                    ansStep10 = "";
                    Toast.makeText(AdminAddChatbotData.this, "Step 10 Removed!", Toast.LENGTH_SHORT).show();
                }
                i[0]--;

            }
        });

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toast();
            }
        });

    }

    public void toast(){

        final String question = editTextQuestion.getText().toString();
        if(TextUtils.isEmpty(question)){
            Toast.makeText(this, "Enter Question!", Toast.LENGTH_SHORT).show(); return;
        }

        final String answerText = answerEditText.getText().toString();
        if(TextUtils.isEmpty(answerText)){
            Toast.makeText(this, "Enter Answer!", Toast.LENGTH_SHORT).show(); return;
        }
        if(editText1.isShown()) {
            String step1 = editText11.getText().toString();
            if (TextUtils.isEmpty(step1)) {
                Toast.makeText(this, "Enter step 1!", Toast.LENGTH_SHORT).show();
                return;
            }
            ansStep1 = "\n Step 1 - " + step1;
        }
        if(editText2.isShown()){
            String step2 = editText22.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 2!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep2 = "\n\n Step 2 - " + step2;
        }

        if(editText3.isShown()){
            String step3 = editText33.getText().toString();
            if(TextUtils.isEmpty(step3)){
                Toast.makeText(this, "Enter step 3!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep3 =  "\n\n Step 3 - " +step3;
        }

        if(editText4.isShown()){
            String step2 = editText44.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 4!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep4 = "\n\n Step 4 - " + step2;
        }

        if(editText5.isShown()){
            String step2 = editText55.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 5!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep5 = "\n\n Step 5 - " + step2;
        }

        if(editText6.isShown()){
            String step2 = editText66.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 6!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep6 = "\n\n Step 6 - " + step2;
        }

        if(editText7.isShown()){
            String step2 = editText77.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 7!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep7 = "\n\n Step 7 - " + step2;
        }

        if(editText8.isShown()){
            String step2 = editText88.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 8!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep8 = "\n\n Step 8 - " + step2;
        }

        if(editText9.isShown()){
            String step2 = editText99.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 9!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep9 = "\n\n Step 9 - " + step2;
        }

        if(editText10.isShown()){
            String step2 = editText100.getText().toString();
            if(TextUtils.isEmpty(step2)){
                Toast.makeText(this, "Enter step 10!", Toast.LENGTH_SHORT).show();return;
            }
            ansStep1 = "\n\n Step 10 - " + step2;
        }


        final String answer = ansStep1+ansStep2+ansStep3+ansStep4+ansStep5+ansStep6+ansStep7+ansStep8+ansStep9+ansStep10;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        ChatBotDetails chatBotDetails = new ChatBotDetails();
                        chatBotDetails.setQuestion(question);
                        chatBotDetails.setAnswer(answerText+answer);
                        databaseReference.push().setValue(chatBotDetails);
                        Toast.makeText(AdminAddChatbotData.this, "Data Added!", Toast.LENGTH_SHORT).show();
                        clearData();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddChatbotData.this);
        builder.setMessage("Are you sure? you want add this Data!").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    public void clearData(){
        answerEditText.setText("");
        editTextQuestion.setText("");
        editText11.setText("");
        editText22.setText("");
        editText33.setText("");
        editText44.setText("");
        editText55.setText("");
        editText66.setText("");
        editText77.setText("");
        editText88.setText("");
        editText99.setText("");
        editText100.setText("");

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


