package in.org.verkstad.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
        sqlDatabase = new SQLDatabase(MainActivity.this,DATABASE_NAME,null,DATABASE_VERSION);
        db = sqlDatabase.getWritableDatabase();

    }

    public void Open_file(View view){
        Intent intent = new Intent(this,TrackLocation.class);
        startActivity(intent);
    }

    public void send_sms(View view){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(receivers_mobile_no.getText().toString(),null,getting_last_location(),null,null);

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setType("vnd.android-dir/mms-sms");
        receivers_mobile_no.setText("");

    }

    public String getting_last_location(){
        Cursor res = db.rawQuery("SELECT * FROM Location",null);
        res.moveToLast();
        String last_location = "latitude:"+res.getDouble(0)+""+"longitude:"+res.getDouble(1)+""+"speed:"+res.getDouble(2)+""+"direction:"+res.getDouble(3)+""+"time:"+res.getString(4);
        return last_location;
    }

}
