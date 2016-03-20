package in.org.verkstad.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText receivers_mobile_no,senders_mobile_no;
    String url = "https://rest.nexmo.com/sms/json";
    SQLDatabase sqlDatabase;
    SQLiteDatabase db;
    String DATABASE_NAME = "LocationTracker";
    int DATABASE_VERSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receivers_mobile_no = (EditText) findViewById(R.id.receivers_Mobile_no);
        senders_mobile_no = (EditText) findViewById(R.id.senders_Mobile_no);
        sqlDatabase = new SQLDatabase(MainActivity.this,DATABASE_NAME,null,DATABASE_VERSION);
        db = sqlDatabase.getWritableDatabase();
       // Toast.makeText(getApplicationContext(),getSharedPreferences("information",MODE_PRIVATE).getString("api_key",null),Toast.LENGTH_SHORT).show();
    }

    public void Open_file(View view){
        Intent intent = new Intent(this,TrackLocation.class);
        startActivity(intent);
    }

    public void send_sms(View view){
        final SharedPreferences sharedPreferences = getSharedPreferences("information",MODE_PRIVATE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                receivers_mobile_no.setText("");
                senders_mobile_no.setText("");
               // Toast.makeText(getApplicationContext(),"Message sent",Toast.LENGTH_LONG).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("api_key",sharedPreferences.getString("api_key",null));
                params.put("api_secret",sharedPreferences.getString("api_secret_key", null));
               // params.put("api_key","cfea2b30");
                //params.put("api_secret","60c8674cf417e248");
                //params.put("from","918954217878");
               //params.put("to","918954217878");
                params.put("from",senders_mobile_no.getText().toString());
                params.put("to",receivers_mobile_no.getText().toString());
                params.put("text",getting_last_location());
               // params.put("text","hiiii");
                return params;
            }
        };
        requestQueue.add(stringRequest);

    }

    public String getting_last_location(){
        Cursor res = db.rawQuery("SELECT * FROM Location",null);
        res.moveToLast();
        String last_location = "latitude:"+res.getDouble(0)+""+"longitude:"+res.getDouble(1)+""+"speed:"+res.getDouble(2)+""+"direction:"+res.getDouble(3)+""+"time:"+res.getString(4);
        return last_location;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,settings.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings,menu);
        return true;
    }
}
