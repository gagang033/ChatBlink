package lenovo.example.com.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Chat extends AppCompatActivity {
    LinearLayout layout,disconnected;
    TextView disconnectedText;
    Button disconnectedButton;
    RelativeLayout layout_2;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1, reference2;
    String checkConnected;
    Boolean hasDisconnected = false;
    String chatWith;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        checkConnected = "https://chat-46918.firebaseio.com/random_chat/isConnected.json";

        layout = (LinearLayout) findViewById(R.id.layout1);
        layout_2 = (RelativeLayout)findViewById(R.id.layout2);

        sendButton = (ImageView) findViewById(R.id.sendButton);
        messageArea = (EditText)findViewById(R.id.messageArea);
        scrollView = (ScrollView)findViewById(R.id.scrollView);

        disconnected = findViewById(R.id.disconnected);
        disconnectedText = findViewById(R.id.disconnected_text);
        disconnectedButton = findViewById(R.id.disconnected_button);

        disconnected.setVisibility(View.GONE);

        Internet internet = new Internet();
        internet.execute();

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://chat-46918.firebaseio.com/random_chat/messages/" + UserDetails.chatWith + "_" + UserDetails.username);

        reference1.onDisconnect().removeValue();
        reference2.onDisconnect().removeValue();

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

        Firebase reference4 = new Firebase("https://chat-46918.firebaseio.com/random_chat/isConnected/" + UserDetails.username);
        //reference4.removeValue();
        reference4.onDisconnect().removeValue();

        Firebase reference5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        reference5.onDisconnect().removeValue();

        do{
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference userNameRef = rootRef.child("random_chat").child("isConnected").child(UserDetails.chatWith);
            userNameRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                    Log.v("ondata","hdgds");
                    if(dataSnapshot.exists()){
                        //Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
                        //sendButton.setClickable(false);
                        //Log.v("insideif"," hasDisconnected = : " + hasDisconnected);
                        //setValue(1);
                        //Log.v("c","value"+c);
                        chatWith = dataSnapshot.child("chatWith").getValue().toString();
                        //isConnected = true;
                        Log.v("newrequest","chatwith "+chatWith);
                        if(!chatWith.equals(UserDetails.username)){
                            //Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
                            hasDisconnected = true;
                            sendButton.setClickable(false);
                            messageArea.setClickable(false);
                            disconnected.setVisibility(View.VISIBLE);
                            disconnectedText.setText(UserDetails.chatWith + " has disconnected.");
                        }
                        //UserDetails.chatWith = chatWith;
                        //NewRequest3();
//                        startActivity(new Intent(Connecting.this, Chat.class));
//                        finish();
                    }
                    //else
                        //NewRequest2();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }while (false);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Internet internet = new Internet();
                internet.execute();

//                if(NewRequest()){
//                    Log.v("NewRequest1"," hasDisconnected = : " + hasDisconnected);
//                    Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
//                    sendButton.setClickable(false);
//                }

                //Firebase.setAndroidContext(this);
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userNameRef = rootRef.child("random_chat").child("isConnected").child(UserDetails.chatWith);
                userNameRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            //Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
                            sendButton.setClickable(false);
                            messageArea.setClickable(false);
                            hasDisconnected = true;
                            //Log.v("insideif"," hasDisconnected = : " + hasDisconnected);
                            disconnected.setVisibility(View.VISIBLE);
                            disconnectedText.setText(UserDetails.chatWith + " has disconnected.");
                        }
                        else {
                            chatWith = dataSnapshot.child("chatWith").getValue().toString();
                            //isConnected = true;
                            Log.v("newrequest","chatwith "+chatWith);
                            if(!chatWith.equals(UserDetails.username)){
                                //Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
                                hasDisconnected = true;
                                sendButton.setClickable(false);
                                messageArea.setClickable(false);
                                disconnected.setVisibility(View.VISIBLE);
                                disconnectedText.setText(UserDetails.chatWith + " has disconnected.");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You:-  " , message, 1);
                }
                else{
                    addMessageBox(UserDetails.chatWith + ":-  " , message, 2);
                }

//                if(userName.equals(UserDetails.username)){
//                    addMessageBox( message, 1);
//                }
//                else{
//                    addMessageBox(message, 2);
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String user ,String message, int type){
        TextView textView = new TextView(Chat.this);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 1.0f;

        if(type == 1) {
            lp2.gravity = Gravity.LEFT;
            //textView.setBackgroundResource(R.drawable.bubble_in);
            Spannable word = new SpannableString(user);
            word.setSpan(new ForegroundColorSpan(Color.BLUE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(word);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            //textView.setBackgroundResource(R.drawable.bubble_out);
            Spannable word = new SpannableString(user);
            word.setSpan(new ForegroundColorSpan(Color.RED), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(word);
        }

        Spannable wordTwo = new SpannableString(message);
        wordTwo.setSpan(new ForegroundColorSpan(Color.BLACK), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.append(wordTwo);

        textView.setLayoutParams(lp2);
        textView.setTextSize(20);
        //textView.setTextColor(getResources().getColor(R.color.colorPrimary));
        layout.addView(textView);
        //layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onStart(){
        super.onStart();

//        if(NewRequest())
//            Toast.makeText(Chat.this,"Disconnected",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onResume(){
        super.onResume();
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
                AlertDialog.Builder alert = new AlertDialog.Builder(Chat.this)
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

    public void NextStranger(View view){
        reference1.removeValue();

        Firebase reference4 = new Firebase("https://chat-46918.firebaseio.com/random_chat/isConnected/" + UserDetails.username);
        reference4.removeValue();

        Firebase reference5 = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        reference5.removeValue();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_disconnect:
                if(!hasDisconnected)
                    showLeaveConfirmationDialog();
                else
                    finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showLeaveConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leave this chat?");
        builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        Firebase.setAndroidContext(this);
        reference1.removeValue();
        reference1.onDisconnect().removeValue();
        reference2.removeValue();
        reference2.onDisconnect().removeValue();

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

        Firebase reference4 = new Firebase("https://chat-46918.firebaseio.com/random_chat/isConnected/" + UserDetails.username);
        reference4.removeValue();
        reference4.onDisconnect().removeValue();

    }

    @Override
    public void onBackPressed(){

        if(hasDisconnected){
            super.onBackPressed();
            return;
        }

            // Otherwise if there are unsaved changes, setup a dialog to warn the user.
            // Create a click listener to handle the user confirming that changes should be discarded.
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User clicked "Discard" button, close the current activity.
                            finish();
                        }
                    };
            // Show dialog that there are unsaved changes
            showOnBackLeaveDialog(discardButtonClickListener);
    }

    private void showOnBackLeaveDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Leave this chat?");
        builder.setPositiveButton("Leave", discardButtonClickListener);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}