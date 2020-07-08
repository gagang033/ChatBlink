package lenovo.example.com.chat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telecom.ConnectionService;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Login extends AppCompatActivity {
    EditText username;
    Button nextButton;
    String user, gender;
    CheckBox checkMale, checkFemale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Random Chat");

        username = (EditText)findViewById(R.id.username);
        nextButton = (Button)findViewById(R.id.nextButton);
        checkMale = findViewById(R.id.check_male);
        checkFemale = findViewById(R.id.check_female);

            Internet internet = new Internet();
            internet.execute();


        checkMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = getString(R.string.gender_male);
                checkMale.setChecked(true);
                checkFemale.setChecked(false);
            }
        });

        checkFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender = getString(R.string.gender_female);
                checkMale.setChecked(false);
                checkFemale.setChecked(true);
            }
        });

        gender = getString(R.string.gender_male);

        Firebase.setAndroidContext(this);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Internet internet = new Internet();
                internet.execute();

                user = username.getText().toString().trim();

                if(!(checkMale.isChecked() || checkFemale.isChecked())  || user.equals("") ){

                    if(user.equals(""))
                        username.setError("can't be blank");

                    if(!(checkMale.isChecked() || checkFemale.isChecked()))
                        Toast.makeText(Login.this,"Please select your gender", Toast.LENGTH_LONG).show();

                }
                if(!user.matches("[A-Za-z0-9 _]+")){
                    username.setError("No special characters allowed");
                }

                else {
                    final ProgressDialog pd = new ProgressDialog(Login.this);
                    pd.setMessage("Loading...");
                    pd.show();

                    String url = "https://chat-46918.firebaseio.com/random_chat/users.json";

                    StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                        @Override
                        public void onResponse(String s) {
                            Firebase reference = new Firebase("https://chat-46918.firebaseio.com/random_chat/users");

                            if(s.equals("null")) {
                                reference.child(user).child("gender").setValue(gender);
                                Toast.makeText(Login.this, "registration successful", Toast.LENGTH_LONG).show();

                                UserDetails.username = user;
                                UserDetails.gender = gender;
                                Intent intent = new Intent(Login.this, ConnectTo.class);
                                intent.putExtra("user",user);
                                intent.putExtra("gender",gender);
                                startActivity(intent);
                            }
                            else {
                                try {
                                    JSONObject obj = new JSONObject(s);

                                    if (!obj.has(user)) {
                                        reference.child(user).child("gender").setValue(gender);
                                        Toast.makeText(Login.this, "registration successful", Toast.LENGTH_LONG).show();

                                        UserDetails.username = user;
                                        UserDetails.gender = gender;
                                        Intent intent = new Intent(Login.this, ConnectTo.class);
                                        intent.putExtra("user",user);
                                        intent.putExtra("gender",gender);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(Login.this, "username already exists", Toast.LENGTH_LONG).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            pd.dismiss();
                        }

                    },new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            System.out.println("" + volleyError );
                            pd.dismiss();
                        }
                    });

                    RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                    rQueue.add(request);
                }
            }
        });
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
                AlertDialog.Builder alert = new AlertDialog.Builder(Login.this)
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
        Firebase reference = new Firebase("https://chat-46918.firebaseio.com/random_chat/users/" + UserDetails.username);
        reference.onDisconnect().removeValue();

        Firebase reference2 = new Firebase("https://chat-46918.firebaseio.com/random_chat/connect/" + UserDetails.gender + "_" + UserDetails.oppositeGender + UserDetails.username);
        reference2.onDisconnect().removeValue();
    }

    public void SetMale(View view){
        gender = getString(R.string.gender_male);
        checkMale.setChecked(true);
        checkFemale.setChecked(false);
    }

    public void SetFemale(View view){
        gender = getString(R.string.gender_female);
        checkFemale.setChecked(true);
        checkMale.setChecked(false);
    }

}
