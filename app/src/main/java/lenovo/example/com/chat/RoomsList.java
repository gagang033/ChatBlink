package lenovo.example.com.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RoomsList extends AppCompatActivity {

    RadioButton hindiChat;
    TextView hindiChatCount;
    RadioButton adultOnly;
    TextView adultOnlyCount;
    RadioButton gamersRoom;
    TextView gamersRoomCount;

    Button enterRooms;

    String hindiCount, adultCount, gamersCount;
    EditText username;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms_list);

        setTitle("Chat Rooms");

        hindiChat = findViewById(R.id.hindi_chat);
        hindiChatCount = findViewById(R.id.hindi_chat_no);
        adultOnly = findViewById(R.id.adult_only);
        adultOnlyCount = findViewById(R.id.adult_only_no);
        gamersRoom = findViewById(R.id.gamers_room);
        gamersRoomCount = findViewById(R.id.gamers_room_no);

        enterRooms = findViewById(R.id.enter_rooms);
        username = findViewById(R.id.username_rooms);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rooms = rootRef.child("rooms");
        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hindiCount = dataSnapshot.child("hindi_chat").child("count").getValue().toString();
                adultCount = dataSnapshot.child("adult_only").child("count").getValue().toString();
                gamersCount = dataSnapshot.child("gamers_room").child("count").getValue().toString();

                hindiChatCount.setText("( " + hindiCount + " )");
                adultOnlyCount.setText("( " + adultCount + " )");
                gamersRoomCount.setText("( " + gamersCount + " )");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        hindiChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RoomsList.this, HindiChat.class);
//                startActivity(intent);
                hindiChat.setChecked(true);
                adultOnly.setChecked(false);
                gamersRoom.setChecked(false);
            }
        });

        adultOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RoomsList.this, AdultOnly.class);
//                startActivity(intent);intent
                hindiChat.setChecked(false);
                adultOnly.setChecked(true);
                gamersRoom.setChecked(false);
            }
        });

        gamersRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(RoomsList.this, GamersRoom.class);
//                startActivity(intent);
                hindiChat.setChecked(false);
                adultOnly.setChecked(false);
                gamersRoom.setChecked(true);
            }
        });

        enterRooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = username.getText().toString().trim();

                if(!(hindiChat.isChecked() || adultOnly.isChecked() || gamersRoom.isChecked())  || user.equals("") ){

                    if(user.equals(""))
                        username.setError("can't be blank");

                    if(!(hindiChat.isChecked() || adultOnly.isChecked() || gamersRoom.isChecked()))
                        Toast.makeText(RoomsList.this,"Please select a chat room", Toast.LENGTH_LONG).show();

                }
                if(!user.matches("[A-Za-z0-9 _]+")){
                    username.setError("No special characters allowed");
                }
                else{
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    final DatabaseReference rooms = rootRef.child("rooms").child("users");
                    rooms.child(user).child("username").setValue(user);
                    UserDetails.username = user;
                    if(hindiChat.isChecked()){
                        Intent intent = new Intent(RoomsList.this, HindiChat.class);
                        startActivity(intent);
                        //finish();
                    }
                    else if(adultOnly.isChecked()){
                        Intent intent = new Intent(RoomsList.this, AdultOnly.class);
                        startActivity(intent);
                        //finish();
                    }
                    else {
                        Intent intent = new Intent(RoomsList.this, GamersRoom.class);
                        startActivity(intent);
                        //finish();
                    }
                }

            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference rooms = rootRef.child("rooms");
        rooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hindiCount = dataSnapshot.child("hindi_chat").child("count").getValue().toString();
                adultCount = dataSnapshot.child("adult_only").child("count").getValue().toString();
                gamersCount = dataSnapshot.child("gamers_room").child("count").getValue().toString();

                hindiChatCount.setText("( " + hindiCount + " )");
                adultOnlyCount.setText("( " + adultCount + " )");
                gamersRoomCount.setText("( " + gamersCount + " )");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
