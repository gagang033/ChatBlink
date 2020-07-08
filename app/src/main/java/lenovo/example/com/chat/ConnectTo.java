package lenovo.example.com.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConnectTo extends AppCompatActivity {
    private String oppositeGender;
    private Button connectButton;
    private String user;
    private String gender;

    private RadioButton buttonMale,buttonFemale,buttonAny;

    Firebase reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to);

        setTitle("Random Chat");

        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        gender = intent.getStringExtra("gender");

        buttonMale = findViewById(R.id.check_other_male);
        buttonFemale = findViewById(R.id.check_other_female);
        buttonAny = findViewById(R.id.check_other_any);

        Internet internet = new Internet();
        internet.execute();

        buttonMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oppositeGender = getString(R.string.gender_male);
                buttonMale.setChecked(true);
                buttonFemale.setChecked(false);
                buttonAny.setChecked(false);
            }
        });

        buttonFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oppositeGender = getString(R.string.gender_female);
                buttonMale.setChecked(false);
                buttonAny.setChecked(false);
                buttonFemale.setChecked(true);
            }
        });

        buttonAny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oppositeGender = getString(R.string.gender_any);
                buttonMale.setChecked(false);
                buttonAny.setChecked(true);
                buttonFemale.setChecked(false);
            }
        });

        connectButton = findViewById(R.id.connectButton);

        Firebase.setAndroidContext(this);
        Firebase reference1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        reference1.onDisconnect().removeValue();

        Firebase reference2 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/" + UserDetails.gender + "_" + UserDetails.oppositeGender + "/" + UserDetails.username);
        reference2.onDisconnect().removeValue();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Internet internet = new Internet();
                internet.execute();

                if(!(buttonMale.isChecked() || buttonFemale.isChecked() || buttonAny.isChecked()))
                    Toast.makeText(ConnectTo.this,"Please select opposite gender", Toast.LENGTH_LONG).show();

                else
                    BaseRequest();
            }
        });

    }

    public void BaseRequest(){
        final ProgressDialog pd = new ProgressDialog(ConnectTo.this);
        pd.setMessage("Loading...");
        pd.show();

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference ref1 = rootRef.child("random_chat").child("users");
        ref1.child(user).child("oppositeGender").setValue(oppositeGender);
        UserDetails.username = user;
        UserDetails.gender = gender;
        UserDetails.oppositeGender = oppositeGender;
        NewRequest();
        pd.dismiss();
        startActivity(new Intent(ConnectTo.this, Connecting.class));
        finish();

    }

    public void NewRequest() {
        String url;

        if (oppositeGender.equals(getString(R.string.gender_any))) {
            Log.e("Bbubukb","anygender");
            if (gender.equals(getString(R.string.gender_male))){
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref1 = rootRef.child("random_chat").child("connect").child("male_male");
                ref1.child(user).child("username").setValue(user);

                DatabaseReference ref2 = rootRef.child("random_chat").child("connect").child("male_female");
                ref2.child(user).child("username").setValue(user);
            }
            else {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref1 = rootRef.child("random_chat").child("connect").child("female_female");
                ref1.child(user).child("username").setValue(user);

                DatabaseReference ref2 = rootRef.child("random_chat").child("connect").child("female_male");
                ref2.child(user).child("username").setValue(user);
            }

        } else {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference ref1 = rootRef.child("random_chat").child("connect");
            //ref1.child(user).child("username").setValue(user);

            Log.e("Bbubukb","else");
            if (gender.equals(getString(R.string.gender_male)) && oppositeGender.equals(getString(R.string.gender_male)))
                ref1.child("male_male").child(user).child("username").setValue(user);

            else if (gender.equals(getString(R.string.gender_male)) && oppositeGender.equals(getString(R.string.gender_female)))
                ref1.child("male_female").child(user).child("username").setValue(user);

            else if (gender.equals(getString(R.string.gender_female)) && oppositeGender.equals(getString(R.string.gender_female)))
                ref1.child("female_female").child(user).child("username").setValue(user);

            else //(gender.equals(getString(R.string.gender_female)) && oppositeGender.equals(getString(R.string.gender_male)))
                ref1.child("female_male").child(user).child("username").setValue(user);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case android.R.id.home:
                Firebase reference = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
                reference.removeValue();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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
                AlertDialog.Builder alert = new AlertDialog.Builder(ConnectTo.this)
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
        Firebase reference1 = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        //reference1.removeValue();
        reference1.onDisconnect().removeValue();

        Firebase reference2 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/" + UserDetails.gender + "_" + UserDetails.oppositeGender + "/" + UserDetails.username);
        //reference2.removeValue();
        reference2.onDisconnect().removeValue();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        Firebase reference = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        reference.removeValue();
        reference.onDisconnect().removeValue();
    }
}
