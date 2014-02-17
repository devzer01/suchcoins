package com.gems.suchcoins;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.gems.suchcoins.util.JSONParser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;



public class MainActivity extends SherlockActivity {
	
	private Context context;
	
	public static final String PREFS_NAME = "SuchCoinsPref";
	
	public static final String POOL_URL = "https://www.suchcoins.com/index.php?page=api&action=getuserstatus&api_key=";
	
	public String poolUrl; 
	
	protected Map<String, String> dataMap;
	
	protected TextView username;

	SharedPreferences settings;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		
		username = (TextView)findViewById(R.id.textUsername);
		
		String apiKey = settings.getString("apiKey", null);
		
		if (apiKey == null) {
			apiKey = "c5e1a17eeb9b41840b2177dbed85c79e0d9e6eabc7a65627ec118ee1b0bc5785";
		}
		
		poolUrl = POOL_URL + apiKey;
		//check if apiKey is set first
		new ProgressTask(MainActivity.this).execute();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	try {
	        	    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	        	    intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

	        	    startActivityForResult(intent, 0);
	        	} catch (Exception e) {    
	        	    Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
	        	    Intent marketIntent = new Intent(Intent.ACTION_VIEW,marketUri);
	        	    startActivity(marketIntent);
	        	}
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            String contents = data.getStringExtra("SCAN_RESULT");
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putString("apiKey", contents);
	            editor.commit();
	        } else {
	        	
	        }
	    }
	}
	
	 private class ProgressTask extends AsyncTask<String, Void, Boolean> {
	        private ProgressDialog dialog;
	 
	        private MainActivity activity;
	 
	        // private List<Message> messages;
	        public ProgressTask(MainActivity activity) {
	            this.activity = activity;
	            context = activity;
	            dialog = new ProgressDialog(context);
	        }
	 
	        private Context context;
	 
	        protected void onPreExecute() {
	            this.dialog.setMessage("Progress start");
	            this.dialog.show();
	        }
	 
	        @Override
	        protected void onPostExecute(final Boolean success) {
	            if (dialog.isShowing()) {
	                dialog.dismiss();
	            }
	            
	            username.setText(dataMap.get("username"));
	        }
	 
	        protected Boolean doInBackground(final String... args) {
	 
	            JSONParser jParser = new JSONParser();
	 
	            // get JSON data from URL
	            JSONObject json = jParser.getJSONFromUrl(poolUrl);
	            
	            dataMap = new HashMap();
	            
	            try {
					JSONObject data = json.getJSONObject("getuserstatus").getJSONObject("data");
					dataMap.put("username", data.getString("username"));
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 
	            return true;
	        }
	    }
}
