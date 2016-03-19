package in.org.verkstad.locationtracker;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import java.util.ArrayList;
import java.util.List;

public class TrackLocation extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, AdapterView.OnItemSelectedListener {
    GoogleApiClient googleApiClient;
    TextView location,speed,direction;
    android.location.Location last_location;
    LocationRequest locationRequest;
    Spinner update_interval_spinner;
    LinearLayout linear_current,linear_updated;
    int update_interval;
    boolean requestingLocationUpdates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        location= (TextView) findViewById(R.id.location);
        speed= (TextView) findViewById(R.id.speed);
        direction= (TextView) findViewById(R.id.direction);
        update_interval_spinner= (Spinner) findViewById(R.id.update_interval_spinner);
        linear_current= (LinearLayout) findViewById(R.id.linear_current);
        linear_updated= (LinearLayout) findViewById(R.id.linear_updated);


        buildGoogleAPIClient();

        display_location();

        createRequestforLocation();
    }

    public void location_updates(View view){
       // linear_current.setVisibility(View.GONE);
       // linear_updated.setVisibility(View.VISIBLE);
        requestingLocationUpdates = true;
        startLocationUpdates();
        //Updating location time list
       // update_interval_spinner.setOnItemSelectedListener(this);
       // List<String> categories  = new ArrayList<String>();
       // categories.add("1 min");
       // categories.add("5 min");
       // ArrayAdapter arrayAdapter = new ArrayAdapter(TrackLocation.this, R.layout.support_simple_spinner_dropdown_item,categories);
       // arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //update_interval_spinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals("1 min")){
            update_interval = 1000*60;
        }
        else {
            update_interval = 1000*60*5;
        }
        //Toast.makeText(getApplicationContext(),""+update_interval,Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(2000);
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
                location.setText(latitude+","+longitude);
            }
            else {
                location.setText("Error getting location.Make sure location is enabled");
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
        last_location = location;
        display_location();
        Toast.makeText(getApplicationContext(),"Location changed",Toast.LENGTH_SHORT).show();
       // Log.d("location changed","called");

    }



}
