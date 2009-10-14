package com.quick.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.DialogInterface.OnDismissListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class QuickSettings extends Activity implements OnClickListener, OnDismissListener  {

	public class BatteryInfo extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			try
			{
				String action = intent.getAction();
				if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {       		
					int level = intent.getIntExtra("level", 0);
					mBatteryLabel.setText("Battery level: "
                             + String.valueOf(level) + "%");
				}
			}catch(Exception e){}
		}
	}

	private TextView mWifiButton = null;
	private TextView mGPSButton = null;
	private TextView mBluetoothButton = null;
	private TextView mThreeGButton = null;
	private TextView mBatteryLabel = null;
	
	private BatteryInfo mBI = null;
	
	private static int ENABLED_TEXT_COLOR = 0xFF008000;
	private static int DISABLED_TEXT_COLOR = 0xFFAA0000;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		LayoutInflater factory = LayoutInflater.from(this);
		final View settingsView = factory.inflate(
				R.layout.settings, null);
		AlertDialog window = new AlertDialog.Builder(this).setView(settingsView).create();
		window.setOnDismissListener(this);

		registerClickListener(settingsView);
		updateButtonState();

		window.show();
		
        if(mBI == null) {
        	mBI = new BatteryInfo();
        	IntentFilter mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
            registerReceiver(mBI, mIntentFilter);
        }
	}
	
	private void registerClickListener(View settingsView) {
		mWifiButton = (TextView) settingsView.findViewById(R.id.wifiView);
		mWifiButton.setOnClickListener(this);
		mGPSButton = (TextView) settingsView.findViewById(R.id.gpsView);
		mGPSButton.setOnClickListener(this);
		mBluetoothButton = (TextView) settingsView.findViewById(R.id.bluetoothView);
		mBluetoothButton.setOnClickListener(this);
		mThreeGButton = (TextView) settingsView.findViewById(R.id.threeG);
		mThreeGButton.setOnClickListener(this);
		mBatteryLabel = (TextView) settingsView.findViewById(R.id.batteryLabel);
		mBatteryLabel.setOnClickListener(this);
	}

	private void updateButtonState() {
		 try
	        {
			 	LocationManager locManager = (LocationManager)getSystemService(LOCATION_SERVICE);
	            boolean gpsOn = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	            mGPSButton.setTextColor(gpsOn?ENABLED_TEXT_COLOR:DISABLED_TEXT_COLOR);
	        	
	        	int btStatus = Settings.Secure.getInt(getContentResolver(), android.provider.Settings.Secure.BLUETOOTH_ON);
	        	mBluetoothButton.setTextColor(btStatus==0?DISABLED_TEXT_COLOR:ENABLED_TEXT_COLOR);
	        	
	        	WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
	        	mWifiButton.setTextColor(wifiManager.isWifiEnabled()?ENABLED_TEXT_COLOR:DISABLED_TEXT_COLOR);
	        	
	        	TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	        	boolean is3G = (manager.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
	        	mThreeGButton.setTextColor(is3G?ENABLED_TEXT_COLOR:DISABLED_TEXT_COLOR);
	        	
	        }catch(Exception e)
	        {
	        	Log.d("BatteryWidget","Failed to get button status",e);
	        }
	}
	 @Override
     public void onResume() {
          super.onResume();
          registerReceiver(mBI, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));
     }
 
     @Override
     public void onPause() {
          super.onPause();
          unregisterReceiver(mBI);
     }


	@Override
	public void onClick(View v) {
		try
		{
			if(v == mWifiButton) {
				WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
				boolean enabled = wifiManager.isWifiEnabled();
				wifiManager.setWifiEnabled(!enabled);
				Toast.makeText(this, enabled?"Disabling Wifi":"Enabling Wifi", Toast.LENGTH_SHORT).show();
				finish();
			} else if( v == mGPSButton) {
				Intent defineIntent2 = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
				defineIntent2.addCategory("android.intent.category.DEFAULT");
				startActivity(defineIntent2);
				finish();
			} else if( v == mBluetoothButton) {
				Intent defineIntent2 = new Intent("android.settings.WIRELESS_SETTINGS");
				defineIntent2.addCategory("android.intent.category.DEFAULT");
				startActivity(defineIntent2);
				finish();
			} else if(v == mThreeGButton) {
				Intent defineIntent2 = new Intent("android.intent.action.MAIN");
				defineIntent2.setClassName("com.android.phone", "com.android.phone.Settings");
				startActivity(defineIntent2);
				finish();
			}
		}catch(Exception e){
			Log.e("SimpleSwitcher","Settings Error",e);
		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		finish();
	}
}