package com.department13.skryfi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Build;

public class HomeScreen extends Activity
{
	private final static String LOG_TAG = "HomeScreen";
	private static final int PERMISSION_REQUEST_CODE = 1001;
	private static final String[] PERMISSIONS = new String[] {
		Manifest.permission.ACCESS_FINE_LOCATION,
		Manifest.permission.ACCESS_COARSE_LOCATION,
		Manifest.permission.ACCESS_WIFI_STATE,
		Manifest.permission.CHANGE_WIFI_STATE
	};
	private static final String[] ANDROID_13_PERMISSIONS = new String[] {
		Manifest.permission.NEARBY_WIFI_DEVICES,
		Manifest.permission.POST_NOTIFICATIONS
	};

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.home);
	
	    ImageButton menuButton = (ImageButton)findViewById(R.id.home_menu_button);
	    menuButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				Log.d(LOG_TAG,"on-click options menu");
				openOptionsMenu();				
			}
		});
	    SurveyManager.getInstance().initDB();
	    
	   TextView network = (TextView)findViewById(R.id.home_knownnetworks);
	   network.setText(String.valueOf(SurveyManager.getInstance().getNetworkCount()));

	   TextView crackedKeys = (TextView)findViewById(R.id.home_keyscracked);
	   crackedKeys.setText(String.valueOf(SurveyManager.getInstance().getCrackedCount()));

	    SurveyManager.getInstance().close();
	   
        new Handler().postDelayed(new Runnable() { 
            public void run() { 
            	openOptionsMenu(); 
            } 
        }, 1000);

        checkAndRequestPermissions();
	}
	
	//Show Menu
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		return true;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		SurveyManager.getInstance().stop();
		SurveyManager.getInstance().close();
	}
	
	//Menu item has been clicked
	public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.home_quit:
        	Log.d(LOG_TAG,"Menu Hit quit");
    		SurveyManager.getInstance().stop();
    		SurveyManager.getInstance().close();
			finish();
            return true;
        case R.id.home_service:
        	Log.d(LOG_TAG,"Menu survey screen");
        	Intent intent = new Intent(this,SurveyListScreen.class);
        	startActivity(intent);
        	return true;
        case R.id.home_networklist:
        	Log.d(LOG_TAG,"Menu survey screen");
        	Intent network = new Intent(this,NetworkListScreen.class);
        	startActivity(network);
        	return true;
        case R.id.home_saveklms:
        	Toast.makeText(this, "Not an available feature at this time", Toast.LENGTH_SHORT).show();
        	return true;
        case R.id.home_clear:
        	SurveyManager.getInstance().close();
        	NetworkDatabase.Delete();
        	Toast.makeText(this, "database deleted", Toast.LENGTH_SHORT).show();
        	SurveyManager.getInstance().initDB();
        	return true;
        case R.id.home_options:
        	Toast.makeText(this, "Not an available at this time", Toast.LENGTH_SHORT).show();
        	return true;
        case R.id.home_networkdevices:
        	Toast.makeText(this, "Not an available at this time", Toast.LENGTH_SHORT).show();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
     }

    private void checkAndRequestPermissions() {
        // Collect all needed permissions
        java.util.List<String> permissionsNeeded = new java.util.ArrayList<>();
        for (String perm : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(perm);
            }
        }
        if (Build.VERSION.SDK_INT >= 33) {
            for (String perm : ANDROID_13_PERMISSIONS) {
                if (ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED) {
                    permissionsNeeded.add(perm);
                }
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied: " + permissions[i], Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
