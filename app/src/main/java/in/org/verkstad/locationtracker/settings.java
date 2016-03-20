package in.org.verkstad.locationtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class settings extends AppCompatActivity {
    EditText api_key;
    EditText api_secret_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        api_key = (EditText) findViewById(R.id.api_key);
        api_secret_key= (EditText) findViewById(R.id.api_secret_key);
    }

    public void save(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("information",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("api_key",api_key.getText().toString());
        editor.putString("api_secret_key", api_secret_key.getText().toString());
        editor.commit();

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
