package com.celyan.skiradar;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.red_folder.phonegap.plugin.backgroundservice.BackgroundService;

/* Nécessaires au GPS */
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.os.Bundle;
import android.content.Intent;
import android.os.Looper; 

/* Nécessaire pour se connecter au serveur */
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.IOException;

import java.util.List;

public class MyService extends BackgroundService {
	
	private final static String TAG = MyService.class.getSimpleName();
	
	private String 	userId	 		= "User";
	private String	posMsg			= "";
	private String	latitude		= "";
	private String	longitude		= "";
	private String	altitude		= "";
	private String	time			= "";
	private String	previousTime	= "";
	
	@Override
	public void onStart(Intent intent, int startId) {

	    super.onStart(intent, startId);
	    addLocationListener();
	    getLastLocation();
	    Log.d(TAG, "MyService onStart");
	}
	
	//Solution trouvée ici : http://www.androidsnippets.com/get-the-phones-last-known-location-using-locationmanager
	private void getLastLocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        List<String> providers = lm.getProviders(true);
        Location l = null;
        for (int i=providers.size()-1; i>=0; i--) {
            l = lm.getLastKnownLocation(providers.get(i));
            if (l != null) break;
        }
        this.altitude 	= "" + l.getAltitude();
        this.latitude 	= "" + l.getLatitude();
        this.longitude 	= "" + l.getLongitude();
        this.time 		= "" + l.getTime();
	}
	
	// Solution trouvée ici : http://stackoverflow.com/questions/8095030/background-service-need-to-send-gps-location-on-server
	private void addLocationListener() {
		Thread triggerService = new Thread(new Runnable(){
	        public void run(){
	            try{
	                Looper.prepare();//Initialise the current thread as a looper.
	                LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

	                Criteria c = new Criteria();
	                c.setAccuracy(Criteria.ACCURACY_FINE);

	                final String PROVIDER = lm.getBestProvider(c, true);

	                LocationListener MyLocationListener = new MyLocationListener();
	                lm.requestLocationUpdates(PROVIDER, 600000, 0, MyLocationListener);
	                Log.d("LOC_SERVICE", "Service RUNNING!");
	                Looper.loop();
	            }catch(Exception ex){
	                ex.printStackTrace();
	            }
	        }
	    }, "LocationThread");
	    triggerService.start();
	}
	
	@Override
	protected JSONObject doWork() {
		JSONObject result = new JSONObject();
		
		try {
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
			String now = df.format(new Date(System.currentTimeMillis())); 

			String msg = "Hello " 
					+ this.userId 
					+ " - it is currently " + now
					+ " and you are here: "
					+ this.posMsg;
			result.put("Message", msg);

			Log.d(TAG, msg);
			
			if( this.previousTime != this.time ) {
				Log.d(TAG, "Time is different: trying to post...");
				// Soluton found here: http://stackoverflow.com/questions/14088095/how-can-i-send-the-gps-and-network-location-cordinates-to-a-server-static-ip/14103445#14103445
				HttpClient httpclient = new DefaultHttpClient();
				HttpGet  htget = new HttpGet(
						"http://www.yann.com/mobile/android-ping.php?" + 
						this.userId + ";" +
						this.altitude + ";" + 
						this.latitude + ";" + 
						this.longitude + ";" +
						this.time 
				);

				try {
			        // Execute HTTP Post Request
			        HttpResponse response = httpclient.execute(htget);
			        String resp = response.getStatusLine().toString();
			        Log.d(TAG, resp);
			        
				} catch (ClientProtocolException e) {
					Log.d(TAG, "Error http ClientProtocolException");
				} catch (IOException e) {
					Log.d(TAG, "Error http IOException");
			    }
				Log.d(TAG, "End of trying to post...");
				this.previousTime = this.time;
			
			} else {
				Log.d(TAG, "Time is the same: no need to post.");
				
			}
			
		} catch (JSONException e) {
		}
		
		return result;	
	}

	@Override
	protected JSONObject getConfig() {
		JSONObject result = new JSONObject();
		
		try {
			result.put("HelloTo", this.userId);
		} catch (JSONException e) {
		}
		
		return result;
	}

	@Override
	protected void setConfig(JSONObject config) {
		try {
			if (config.has("HelloTo"))
				this.userId = config.getString("HelloTo");
		} catch (JSONException e) {
		}
		
	}     

	@Override
	protected JSONObject initialiseLatestResult() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onTimerEnabled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onTimerDisabled() {
		// TODO Auto-generated method stub
		
	}

	/* Class My Location Listener */
	public class MyLocationListener implements LocationListener {
    	
		public String mLastPos = "";
    	
		@Override
		public void onLocationChanged(Location loc) {
			loc.getAltitude();
			loc.getLatitude();
			loc.getLongitude();
			loc.getTime();
			String Text = "My current location is: " +
					"Elevation = " + loc.getAltitude() +
					"Latitud = " + loc.getLatitude() +
					"Longitud = " + loc.getLongitude();
			Log.d( TAG, Text );
			this.mLastPos = Text;
			MyService.this.posMsg 		= Text;
			MyService.this.altitude 	= "" + loc.getAltitude();
			MyService.this.latitude 	= "" + loc.getLatitude();
			MyService.this.longitude 	= "" + loc.getLongitude();
			MyService.this.time 		= "" + loc.getTime();
		}

		@Override
		public void onProviderDisabled(String provider) {
			String Text = "Gps Disabled";
			Log.d( TAG, Text );
		}

		@Override
		public void onProviderEnabled(String provider) {
			String Text = "Gps Enabled";
			Log.d( TAG, Text );
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

    }/* End of Class MyLocationListener */

}
