package in.org.verkstad.locationtracker;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    GoogleApiClient googleApiClient;
    android.location.Location last_location;
    LocationRequest locationRequest;
    Spinner update_interval_spinner;
    int update_interval = 1000*60;
    RecyclerView recyclerView;
    boolean requestingLocationUpdates;
    RelativeLayout relativeLayout,relative_spinner;
    SQLDatabase sqlDatabase;
    SQLiteDatabase db;
    String DATABASE_NAME = "LocationTracker";
    int DATABASE_VERSION = 2;
    String data;
    ArrayList<String> data_external;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        update_interval_spinner= (Spinner) findViewById(R.id.update_interval_spinner);
        recyclerView= (RecyclerView) findViewById(R.id.recyclerview);
        sqlDatabase = new SQLDatabase(TrackLocation.this,DATABASE_NAME,null,DATABASE_VERSION);
        relativeLayout= (RelativeLayout) findViewById(R.id.relative);
        relative_spinner= (RelativeLayout) findViewById(R.id.relative_spinner);
        db = sqlDatabase.getWritableDatabase();
        //Toast.makeText(getApplicationContext(),""+sqlDatabase.Insert(db),Toast.LENGTH_SHORT).show();
        update_interval_spinner.setPrompt("Select time interval");
        //update_interval_spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("SELECT");
        categories.add("1 min");
        categories.add("5 min");
        ArrayAdapter arrayAdapter = new ArrayAdapter(TrackLocation.this, R.layout.support_simple_spinner_dropdown_item,categories);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        update_interval_spinner.setAdapter(arrayAdapter);

        update_interval_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

                if (item.equals("SELECT")) {
                    return;
                }
                if (item.equals("1 min")) {
                    update_interval = 1000 * 60;
                    relativeLayout.setVisibility(View.VISIBLE);
                    relative_spinner.setVisibility(View.GONE);
                    createRequestforLocation();
                } else {
                    update_interval = 1000*60*5;
                    relativeLayout.setVisibility(View.VISIBLE);
                    relative_spinner.setVisibility(View.GONE);
                    createRequestforLocation();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        buildGoogleAPIClient();
        display_location();
        createRequestforLocation();

    }

    public void location_updates(View view){
        //update_interval_spinner.setVisibility(View.VISIBLE);
        requestingLocationUpdates = true;
        startLocationUpdates();
        //Updating location time list
    }

    public void stop_location_updates(View view){
        requestingLocationUpdates = false;
        stopLocationUpdates();
    }

    protected synchronized void buildGoogleAPIClient(){
        googleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).
                build();
    }

    protected void createRequestforLocation(){
        locationRequest = new LocationRequest();
        locationRequest.setInterval(update_interval);
        locationRequest.setFastestInterval(update_interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);

    }

    protected void startLocationUpdates(){
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        catch (SecurityException e){
            Toast.makeText(getApplicationContext(),"exception"+e,Toast.LENGTH_SHORT ).show();
        }

    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed", "Error:" + connectionResult.getErrorCode());

    }

    @Override
    public void onConnected(Bundle bundle) {
        display_location();
        if(requestingLocationUpdates){
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }


   /** public boolean checkGooglePlayServices(){
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(result)){
                GooglePlayServicesUtil.getErrorDialog(result, this, 1000).show();
            }
            else {
                Toast.makeText(getApplicationContext(),"Device Not Supported",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return true;
    } **/

    public void display_location(){
        try{
            last_location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(last_location != null){
                double latitude = last_location.getLatitude();
                double longitude = last_location.getLongitude();
                long time = last_location.getTime();
                Date date = new Date(time);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String text = sdf.format(date);
                double direction = last_location.bearingTo(last_location);
                double speed = last_location.getSpeed();
                double check = sqlDatabase.Insert(db,latitude,longitude,speed,direction,text);
                //Toast.makeText(getApplicationContext(),""+check,Toast.LENGTH_SHORT).show();
                retrieving_data_from_database();
            }
            else {
               // Toast.makeText(getApplicationContext(),"Error getting location,make sure location is enabled",Toast.LENGTH_SHORT).show();
            }
        }
        catch (SecurityException e){
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(googleApiClient != null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        long time = location.getTime();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String text = sdf.format(date);
        double direction = location.bearingTo(last_location);
        last_location = location;
        double speed = location.getSpeed();
        //Toast.makeText(getApplicationContext(),"Location changed.Your speed is"+speed+"Direction is"+direction+"time is"+text,Toast.LENGTH_SHORT).show();
        double check = sqlDatabase.Insert(db,last_location.getLatitude(),last_location.getLongitude(),speed,direction,text);
        //Toast.makeText(getApplicationContext(),""+check,Toast.LENGTH_SHORT).show();
        retrieving_data_from_database();
    }

    public void retrieving_data_from_database(){
        ArrayList<Double> latitude = new ArrayList<Double>();
        ArrayList<Double> longitude = new ArrayList<Double>();
        ArrayList<Double> direction = new ArrayList<Double>();
        ArrayList<Double> speed = new ArrayList<Double>();
        ArrayList<String> time = new ArrayList<String >();
        data_external = new ArrayList<String>();
        Cursor res = db.rawQuery("SELECT * FROM Location", null);
        res.moveToFirst();
        data_external.clear();
        while (res.isAfterLast()==false){
            latitude.add(res.getDouble(0));
            longitude.add(res.getDouble(1));
            speed.add(res.getDouble(2));
            direction.add(res.getDouble(3));
            time.add(res.getString(4));
            data_external.add("latitude:"+ res.getDouble(0) +""+ "longitude:" + res.getDouble(1) +""+ "speed:" + res.getDouble(2) +""+ "direction:" + res.getDouble(3) +""+ "time:" + res.getString(4));
            res.moveToNext();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(TrackLocation.this,latitude,longitude,speed,direction,time);
        recyclerView.setAdapter(adapter);


    }

    public void save(View view){
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File myFile = new File(folder,"Locationtracker.txt");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream=new FileOutputStream(myFile);
            fileOutputStream.write(data_external.toString().getBytes());
            Toast.makeText(getApplicationContext(),"DATA SAVED TO SDcard/DCIM/Locationtracker.txt",Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error,check your sdcard",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error,check your sdcard",Toast.LENGTH_SHORT).show();
        }

        finally {
            if (fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
