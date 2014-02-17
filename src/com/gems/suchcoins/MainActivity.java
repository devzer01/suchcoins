package com.gems.suchcoins;

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



public class MainActivity extends SherlockActivity {
	
	private Context context;
	
	public static final String PREFS_NAME = "SuchCoinsPref";
	
	public static final String POOL_URL = "https://www.suchcoins.com/index.php?page=api&action=getpoolstatus&api_key=";
	
	public String poolUrl; 
	

	SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String apiKey = settings.getString("apiKey", null);
		
		if (apiKey.equals(null)) {
			apiKey = "SCAN QR CODE TO ENTER API KEY";
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
	        }
	 
	        protected Boolean doInBackground(final String... args) {
	 
	            JSONParser jParser = new JSONParser();
	 
	            // get JSON data from URL
	            JSONArray json = jParser.getJSONFromUrl(poolUrl);
	 
	            for (int i = 0; i < json.length(); i++) {
	 
	                try {
	                    JSONObject c = json.getJSONObject(i);
	                    String vtype = c.getString("test");
	                }
	                catch (JSONException e) {
	                    e.printStackTrace();
	                }
	            }
	            return null;
	        }
	    }
}
