package lenovo.example.com.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Random;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Connecting extends AppCompatActivity {
    private String gender;
    private String oppositeGender;

   // private String checkConnected;
    private String chatWith;
    private Boolean isConnected = false;

    long totalSeconds = 60;
    long intervalSeconds = 1;
    CountDownTimer timer;

    Boolean stop = false;

    //ImageView v;

    ArrayList<String> al = new ArrayList<>();
    int totalUsers = 0;
    private int c = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);

        setTitle("Random Chat");

//        v = findViewById(R.id.v);
//        v.setVisibility(View.VISIBLE);

        gender = UserDetails.gender;
        oppositeGender = UserDetails.oppositeGender;

        //checkConnected = "https://chat-46918.firebaseio.com/random_chat/isConnected.json";

        timer = new CountDownTimer(totalSeconds * 1000, intervalSeconds * 1000) {

            public void onTick(long millisUntilFinished) {
                //Log.d("seconds elapsed: " , (totalSeconds * 1000 - millisUntilFinished) / 1000);
            }

            public void onFinish() {
                //Log.d( "done!", "Time's up!");
                stop = true;
                AlertDialog.Builder alert = new AlertDialog.Builder(Connecting.this)
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //moveTaskToBack(true);
                                Firebase reference1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
                                reference1.removeValue();
                                finish();
                            }
                        });

                if (oppositeGender.equals(getString(R.string.gender_male)))
                    alert.setMessage("Oops!! Looks like no males are available at this moment" + "\n\nPlease try again later!!");
                else if (oppositeGender.equals(getString(R.string.gender_female)))
                    alert.setMessage("Oops!! Looks like no females are available at this moment" + "\n\n Please try again later!!");
                else
                   alert.setMessage("Oops!! Looks like no one is available at this moment" + "\n\nPlease try again later!!");

                alert.show();
            }

        };

        timer.start();

        Firebase reference = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        //reference.removeValue();
        reference.onDisconnect().removeValue();

        Firebase r5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.username);
        //r5.removeValue();
        r5.onDisconnect().removeValue();

        Firebase r6 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.username);
        //r6.removeValue();
        r6.onDisconnect().removeValue();

        Firebase r7 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.username);
        //r7.removeValue();
        r7.onDisconnect().removeValue();

        Firebase r8 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.username);
        //r8.removeValue();
        r8.onDisconnect().removeValue();

        Internet internet = new Internet();
        internet.execute();

        NewRequest();

    }


    @Override
    protected void onStart(){
        super.onStart();

        Internet internet = new Internet();
        internet.execute();

    }

    public void NewRequest(){
        al.clear();
        if (stop){
            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                Log.v("Newrequest","called");
                isConnected = false;
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userNameRef = rootRef.child("random_chat").child("isConnected").child(UserDetails.username);
                userNameRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        Log.v("ondata","hdgds");
                        if(dataSnapshot.exists()){
                            //Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
                            //sendButton.setClickable(false);
                            //Log.v("insideif"," hasDisconnected = : " + hasDisconnected);
                            setValue(1);
                            Log.v("c","value"+c);
                            chatWith = dataSnapshot.child("chatWith").getValue().toString();
                            isConnected = true;
                            Log.v("newrequest","chatwith "+chatWith);
                            UserDetails.chatWith = chatWith;

                            NewRequest3();
                            timer.cancel();
                            startActivity(new Intent(Connecting.this, Chat.class));
                            finish();
                        }
                        else{
                            Handler h = new Handler();
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(oppositeGender.equals(getString(R.string.gender_any))) {
                                        Log.e("called","any");
                                        NewRequestAny();

                                    }
                                    else{
                                        Log.e("called","non any");
                                        NewRequest2();
                                        //Log.e("called","non any");
                                    }
                                }
                            },500);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }, 1000);   //1 seconds

    }

    public void NewRequestAny(){

        Log.e("newreuest any","called");
        if(gender.equals(getString(R.string.gender_male))){
            Log.e("gender","male");
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = rootRef.child("random_chat").child("connect").child("male_male");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.child("username").getValue(String.class);
                        Log.e("TAG", "name: "+name);
                        if(!name.equals(UserDetails.username)) {
                            al.add(name);
                            totalUsers++;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference usersRef2 = rootRef.child("random_chat").child("connect").child("female_male");
            usersRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.child("username").getValue(String.class);
                        Log.e("TAG", "name: "+name);
                        al.add(name);
                        totalUsers++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else{
            Log.e("gender","female");
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersRef = rootRef.child("random_chat").child("connect").child("female_female");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.child("username").getValue(String.class);
                        Log.d("TAG", name);
                        if(!name.equals(UserDetails.username)) {
                            al.add(name);
                            totalUsers++;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            DatabaseReference usersRef2 = rootRef.child("random_chat").child("connect").child("male_female");
            usersRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()){
                        String name = ds.child("username").getValue(String.class);
                        Log.d("TAG", name);
                        al.add(name);
                        totalUsers++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        if(totalUsers <1){

//            Log.e("total users if","t "+totalUsers);
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    // yourMethod();
//                    NewRequest();
//                }
//            }, 1000);   //1 seconds

            NewRequest();

        }
        else{

            Log.e("total users else ","t "+totalUsers);
            UserDetails.chatWith = anyUser();
            if (UserDetails.chatWith.equals("exp")){
                NewRequest();
            }
            Log.e("newrequestany","chatwith "+UserDetails.chatWith);

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userNameRef = rootRef.child("random_chat").child("isConnected").child(UserDetails.chatWith);
            userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                // yourMethod();
//                                NewRequest();
//                            }
//                        }, 1000);   //1 seconds
                        NewRequest();
                    }
                    else {
                        isConnected = true;

                        //Firebase.setAndroidContext(this);
                        Firebase r5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.username);
                        r5.removeValue();
                        r5.onDisconnect().removeValue();

                        Firebase r6 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.username);
                        r6.removeValue();
                        r6.onDisconnect().removeValue();

                        Firebase r7 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.username);
                        r7.removeValue();
                        r7.onDisconnect().removeValue();

                        Firebase r8 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.username);
                        r8.removeValue();
                        r8.onDisconnect().removeValue();

                        Firebase r1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.chatWith);
                        r1.removeValue();

                        Firebase r2 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.chatWith);
                        r2.removeValue();

                        Firebase r3 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.chatWith);
                        r3.removeValue();

                        Firebase r4 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.chatWith);
                        r4.removeValue();

                        NewRequest3();
                        timer.cancel();
                        startActivity(new Intent(Connecting.this, Chat.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void NewRequest2(){
        Log.v("Newrequest2","called");

        Log.v("yyyyyyyyyy","uuuuuuuuuuuuuuu");
        gender = UserDetails.gender;
        oppositeGender = UserDetails.oppositeGender;


        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef;

        if(gender.equals(getString(R.string.gender_male)) && oppositeGender.equals(getString(R.string.gender_male)))
            usersRef = rootRef.child("random_chat").child("connect").child("male_male");

        else if(gender.equals(getString(R.string.gender_male)) && oppositeGender.equals(getString(R.string.gender_female)))
            usersRef = rootRef.child("random_chat").child("connect").child("female_male");

        else if(gender.equals(getString(R.string.gender_female)) && oppositeGender.equals(getString(R.string.gender_female)))
            usersRef = rootRef.child("random_chat").child("connect").child("female_female");

        else //(gender.equals(getString(R.string.gender_female)) && oppositeGender.equals(getString(R.string.gender_male)))
            usersRef = rootRef.child("random_chat").child("connect").child("male_female");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                for (com.google.firebase.database.DataSnapshot ds : dataSnapshot.getChildren()){
                    String name = ds.child("username").getValue(String.class);
                    Log.d("TAG", name);
                    if(!name.equals(UserDetails.username)) {
                        al.add(name);
                        totalUsers++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(totalUsers <1){

//            Log.e("total users if","t "+totalUsers);
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    // yourMethod();
//                    NewRequest();
//                }
//            }, 1000);   //1 seconds
            NewRequest();

        }
        else{

            Log.e("total users else ","t "+totalUsers);
            UserDetails.chatWith = anyUser();
            if (UserDetails.chatWith.equals("exp")){
                NewRequest();
            }
            Log.e("newrequestany","chatwith "+UserDetails.chatWith);

            DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userNameRef = rootReference.child("random_chat").child("isConnected").child(UserDetails.chatWith);
            userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            public void run() {
//                                // yourMethod();
//                                NewRequest();
//                            }
//                        }, 1000);   //1 seconds
                        NewRequest();
                    }
                    else {
                        isConnected = true;

                        //Firebase.setAndroidContext(this);
                        Firebase r5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.username);
                        r5.removeValue();
                        r5.onDisconnect().removeValue();

                        Firebase r6 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.username);
                        r6.removeValue();
                        r6.onDisconnect().removeValue();

                        Firebase r7 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.username);
                        r7.removeValue();
                        r7.onDisconnect().removeValue();

                        Firebase r8 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.username);
                        r8.removeValue();
                        r8.onDisconnect().removeValue();

                        Firebase r1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.chatWith);
                        r1.removeValue();

                        Firebase r2 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.chatWith);
                        r2.removeValue();

                        Firebase r3 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.chatWith);
                        r3.removeValue();

                        Firebase r4 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.chatWith);
                        r4.removeValue();

                        NewRequest3();
                        timer.cancel();
                        startActivity(new Intent(Connecting.this, Chat.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    public String anyUser()
    {
        try {
            Random r = new Random();
            int index = r.nextInt(al.size());
            String chatWith = al.get(index);
            return chatWith;
        }
        catch (Exception exp){
            return "exp";
        }
    }

    public void NewRequest3(){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref1 = rootRef.child("random_chat").child("isConnected");
        ref1.child(UserDetails.chatWith).child("chatWith").setValue(UserDetails.username);

        DatabaseReference ref2 = rootRef.child("random_chat").child("isConnected");
        ref2.child(UserDetails.username).child("chatWith").setValue(UserDetails.chatWith);
    }

    public void setValue(int c){

        this.c = c;
    }


    private class Internet extends AsyncTask<Void,Boolean,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            return isInternetAvailable();
        }

        @Override
        protected void onPostExecute(Boolean result){
            Log.v("p","net"+result);
            if(!result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Connecting.this)
                        .setTitle("Connection Not Found")
                        .setMessage("Please check your internet connection and try again later")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moveTaskToBack(true);
                                finish();
                            }
                        });
                alert.show();
            }
        }
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        timer.cancel();

        Firebase reference1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        //reference1.removeValue();
        reference1.onDisconnect().removeValue();

        Firebase r5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.username);
        r5.removeValue();
        r5.onDisconnect().removeValue();

        Firebase r6 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.username);
        r6.removeValue();
        r6.onDisconnect().removeValue();

        Firebase r7 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.username);
        r7.removeValue();
        r7.onDisconnect().removeValue();

        Firebase r8 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.username);
        r8.removeValue();
        r8.onDisconnect().removeValue();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        timer.cancel();

        Firebase reference = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        reference.removeValue();
        reference.onDisconnect().removeValue();

        Firebase r5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_female/" + UserDetails.username);
        r5.removeValue();
        r5.onDisconnect().removeValue();

        Firebase r6 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_male/" + UserDetails.username);
        r6.removeValue();
        r6.onDisconnect().removeValue();

        Firebase r7 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/female_male/" + UserDetails.username);
        r7.removeValue();
        r7.onDisconnect().removeValue();

        Firebase r8 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/male_female/" + UserDetails.username);
        r8.removeValue();
        r8.onDisconnect().removeValue();
    }
}
