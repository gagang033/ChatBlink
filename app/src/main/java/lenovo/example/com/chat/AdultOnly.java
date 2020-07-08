package lenovo.example.com.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class AdultOnly extends AppCompatActivity {
    ScrollView adultOnlyScrollView;
    RelativeLayout adultOnlyLayout2;
    LinearLayout adultOnlyLayout1;
    String adultCount;
    int count;
    Firebase reference1;
    ImageView sendButton;
    EditText messageArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adult_only);

        Firebase.setAndroidContext(this);

        setTitle("Adult Only");

        adultOnlyScrollView = findViewById(R.id.scrollView_adult_only);
        adultOnlyLayout1 = findViewById(R.id.adult_only_layout1);
        adultOnlyLayout2 = findViewById(R.id.adult_only_layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);

        Internet internet = new Internet();
        internet.execute();

        Firebase reference = new Firebase("https://chat-46918.firebaseio.com/rooms/users/" + UserDetails.username);
        reference.onDisconnect().removeValue();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference rooms = rootRef.child("rooms");
        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adultCount = dataSnapshot.child("adult_only").child("count").getValue().toString();

                count = Integer.parseInt(adultCount);

                count++;

                adultCount = Integer.toString(count);

                rooms.child("adult_only").child("count").setValue(adultCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageArea.getText().toString();

                if(!messageText.equals("")){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://chat-46918.firebaseio.com/rooms/adult_only/messages");
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You:-  " , message, 1);
                }
                else{
                    addMessageBox(userName + ":-  " , message, 2);
                }

//                if(userName.equals(UserDetails.username)){
//                    addMessageBox( message, 1);
//                }
//                else{
//                    addMessageBox(message, 2);
//                }
            }

            @Override
            public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String user ,String message, int type){
        TextView textView = new TextView(AdultOnly.this);

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
        adultOnlyLayout1.addView(textView);
        //layout.addView(textView);
        adultOnlyScrollView.fullScroll(View.FOCUS_DOWN);
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private class Internet extends AsyncTask<Void,Boolean,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            return isInternetAvailable();
        }

        @Override
        protected void onPostExecute(Boolean result){
            Log.v("p","net"+result);
            if(!result) {
                AlertDialog.Builder alert = new AlertDialog.Builder(AdultOnly.this)
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

        Firebase.setAndroidContext(this);
        Firebase reference = new Firebase("https://chat-46918.firebaseio.com/rooms/users/" + UserDetails.username);
        reference.onDisconnect().removeValue();
        reference.removeValue();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference rooms = rootRef.child("rooms");
        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adultCount = dataSnapshot.child("adult_only").child("count").getValue().toString();

                count = Integer.parseInt(adultCount);

                count--;

                adultCount = Integer.toString(count);

                rooms.child("adult_only").child("count").setValue(adultCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
