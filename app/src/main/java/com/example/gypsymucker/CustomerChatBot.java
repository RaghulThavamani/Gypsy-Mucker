package com.example.gypsymucker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomerChatBot extends AppCompatActivity {

    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    List<ResponseMessage> responseMessageList;
    AutoCompleteTextView userInput;
    DatabaseReference myRef,myRefPassword;

    ArrayList<String> mylist = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_chat_bot);


        BottomNavigationView navigation1 = findViewById(R.id.bottom_navigation);
        navigation1.setSelectedItemId(R.id.chatBot);
        navigation1.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener1);

        myRef = FirebaseDatabase.getInstance().getReference("ChatBot");

        userInput = findViewById(R.id.userInput);
        recyclerView = findViewById(R.id.recycler_view);
        responseMessageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(responseMessageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(messageAdapter);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mylist);
        userInput.setAdapter(adapter);

        myRefPassword = FirebaseDatabase.getInstance().getReference("ChatBot");

        myRefPassword.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();

                while (iterator.hasNext()) {
                    DataSnapshot next = (DataSnapshot) iterator.next();


                   String data1= next.child("question").getValue().toString();

                    mylist.add(data1); //this adds an element to the list.

                    System.out.println("DATA1= " + data1);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        String welcome = "How can i help you..\uD83D\uDE0A";
        ResponseMessage responseMessage = new ResponseMessage(welcome.toString(), false);
        responseMessageList.add(responseMessage);

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    int length = userInput.getText().length();
                    if (length > 0) {
                        ResponseMessage responseMessage = new ResponseMessage(userInput.getText().toString(), true);
                        responseMessageList.add(responseMessage);
                        String question = userInput.getText().toString().trim();
                        myRef.orderByChild("question").equalTo(question).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
                                    String ans = data.child("answer").getValue().toString();
                                    ResponseMessage responseMessage2 = new ResponseMessage(ans, false);
                                    responseMessageList.add(responseMessage2);
                                    messageAdapter.notifyDataSetChanged();
                                    userInput.setText("");
                                    scroller(); }
                                } else {
                                    String fake = "Sorry Iam Learning...";
                                    final ResponseMessage responseMessage2 = new ResponseMessage(fake.toString(), false);
                                    responseMessageList.add(responseMessage2);
                                    messageAdapter.notifyDataSetChanged();
                                    userInput.setText("");
                                    scroller();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        scroller();

                    }
                }




                return false;
            }
        });


    }

    boolean isLastVisible() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) recyclerView.getLayoutManager());
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();
        int numItems = recyclerView.getAdapter().getItemCount();
        return (pos >= numItems); }

    public void scroller(){

        if (!isLastVisible())
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener1
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){

                case R.id.chatBot:
                    return true;

                case R.id.notification:
                    startActivity(new Intent(getApplicationContext(),CustomerNotification.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.waitingHall:
                    startActivity(new Intent(getApplicationContext(),CustomerServiceHall.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.map:
                    startActivity(new Intent(getApplicationContext(),CustomerHomePage.class));
                    overridePendingTransition(0,0);
                    return true;

                case R.id.settings:
                    PopupMenu pum = new PopupMenu(CustomerChatBot.this, findViewById(R.id.settings));
                    pum.inflate(R.menu.customer_pop_up_menu);
                    pum.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.cusLogout:
                                    FirebaseAuth.getInstance().signOut();
                                    finish();
                                    Intent intent = new Intent(getApplicationContext(),GmMainLoginPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    break;
                            }
                            return true;
                        }
                    });
                    pum.show();
                    return true;

            }
            return true;
        }
    };

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
