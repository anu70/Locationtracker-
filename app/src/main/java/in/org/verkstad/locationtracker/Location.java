package in.org.verkstad.locationtracker;

import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class Location extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient googleApiClient;
    TextView location,speed,direction;
    android.location.Location last_location;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        location= (TextView) findViewById(R.id.location);

        speed= (TextView) findViewById(R.id.speed);
        direction= (TextView) findViewById(R.id.direction);


        buildGoogleAPIClient();

        display_location();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Connection failed", "Error:" + connectionResult.getErrorCode());

    }

    @Override
    public void onConnected(Bundle bundle) {
        display_location();
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
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
    public void onLocationChanged(android.location.Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
