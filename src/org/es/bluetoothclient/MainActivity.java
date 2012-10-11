package org.es.bluetoothclient;

import static android.view.Window.FEATURE_INDETERMINATE_PROGRESS;
import static org.es.bluetoothclient.services.BluetoothService.STATE_NONE;

import org.es.bluetoothclient.services.BluetoothService;
import org.es.bluetoothclient.utils.IntentKey;
import org.es.bluetoothclient.utils.Log;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Main activity of the BluetoothClient application.
 * The purpose of the application is to send data through Bluetooth.
 * 
 * @author Cyril Leroux
 *
 */
public class MainActivity extends Activity implements OnClickListener {
	private static final String TAG	= "MainActivity";

	// Message types sent from the BluetoothChatService Handler
	/** Message for state change */
	public static final int MESSAGE_STATE_CHANGE	= 1;
	/** Message for read */
	public static final int MESSAGE_READ			= 2;
	/** Message for write */
	public static final int MESSAGE_WRITE			= 3;
	/** Message for device name */
	public static final int MESSAGE_DEVICE_NAME		= 4;
	/** Message for toast */
	public static final int MESSAGE_TOAST			= 5;

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE		= 0;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE	= 1;
	private static final int REQUEST_ENABLE_BT					= 2;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothService mBluetoothService;
	private EditText mEtConsole;
	private EditText mEtMessage;

	/** The Handler that gets information back from the BluetoothService */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_STATE_CHANGE:
				Log.debug(TAG, "MESSAGE_STATE_CHANGE");
				stateChanged(msg.arg1);
				break;

			case MESSAGE_WRITE:
				Log.debug(TAG, "MESSAGE_WRITE");
				break;

			case MESSAGE_READ:
				Log.debug(TAG, "MESSAGE_READ");
				break;

			case MESSAGE_DEVICE_NAME:
				Log.debug(TAG, "MESSAGE_DEVICE_NAME");
				break;

			case MESSAGE_TOAST:
				Log.debug(TAG, "MESSAGE_TOAST : " + msg.getData().getString(IntentKey.TOAST));
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, R.string.bt_not_supported_leaving, Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		((Button) findViewById(R.id.btnLightOn)).setOnClickListener(this);
		((Button) findViewById(R.id.btnLightOff)).setOnClickListener(this);
		((Button) findViewById(R.id.btnSend)).setOnClickListener(this);

		mEtConsole = (EditText) findViewById(R.id.etConsole);
		mEtMessage = (EditText) findViewById(R.id.etTextToSend);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.error(TAG, "++ ON START ++");

		// If Bluetooth is not on, request that it be enabled.
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		} else { // If Bluetooth is on, init Bluetooth service
			if (mBluetoothService == null) {
				initBluetoothService();
			}
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		Log.error(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
		if (mBluetoothService == null) {
			return;
		}

		// Only if the state is STATE_NONE, do we know that we haven't started already
		if (STATE_NONE == mBluetoothService.getState()) {
			// Start the Bluetooth chat services
			mBluetoothService.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.error(TAG, "--- ON DESTROY ---");
		
		// Stop the Bluetooth chat services
		if (mBluetoothService != null) {
			mBluetoothService.stop();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.miSearchBTDevices:
			listDevicesToConnect(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLightOn :
			sendMessage("1");
			break;
		case R.id.btnLightOff :
			sendMessage("0");
			break;
		case R.id.btnSend :
			final String message = mEtMessage.getText().toString();
			mEtMessage.getText().clear();
			sendMessage(message);
			break;

		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.debug(TAG, "onActivityResult " + resultCode);
		
		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == RESULT_OK) {
				connectDevice(data, true);
			}
			break;
		case REQUEST_CONNECT_DEVICE_INSECURE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == RESULT_OK) {
				connectDevice(data, false);
			}
			break;
		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				initBluetoothService();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.error(TAG, "Bluetooth not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
		}
	}

	private void listDevicesToConnect(boolean secure) {
		if (secure) {
			startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE_SECURE);
		} else {
			startActivityForResult(new Intent(this, DeviceListActivity.class), REQUEST_CONNECT_DEVICE_INSECURE);
		}
	}

	private void connectDevice(Intent data, boolean secure) {
		final String address = data.getExtras().getString(IntentKey.EXTRA_DEVICE_ADDRESS);
		if (secure) {
			mEtConsole.getText().append("Connect secure to " + address + "\n");
		} else {
			mEtConsole.getText().append("Connect insecure to " + address + "\n");
		}
		BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
		mBluetoothService.connect(device, secure);
	}

	private void initBluetoothService() {
		Log.debug(TAG, "initBluetoothService()");

		// Initialize the BluetoothService to perform Bluetooth connections
		mBluetoothService = new BluetoothService(getApplicationContext(), mHandler);
	}

	/**
	 * Sends a message.
	 * @param message The message to send over bluetooth.
	 */
	private void sendMessage(final String message) {
		Log.debug(TAG, "sendMessage() : " + message);

		// Check that we're actually connected before trying anything
		if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		// Check that there's actually something to send
		if (message.length() > 0) {
			mEtConsole.getText().append("Sending : " + message + "\n");
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mBluetoothService.write(send);
		}
	}

	private void stateChanged(int message) {
		switch (message) {

		case BluetoothService.STATE_CONNECTED:
			Log.debug(TAG, "Current state : CONNECTED");
			mEtConsole.getText().append("State : CONNECTED\n");
			setProgressBarIndeterminateVisibility(false);
			break;

		case BluetoothService.STATE_CONNECTING:
			Log.debug(TAG, "Current state : CONNECTING");
			mEtConsole.getText().append("State : CONNECTING\n");
			setProgressBarIndeterminateVisibility(true);
			break;

		case BluetoothService.STATE_LISTEN:
			Log.debug(TAG, "Current state : LISTEN");
			mEtConsole.getText().append("State : LISTEN\n");
			setProgressBarIndeterminateVisibility(false);
			break;

		case BluetoothService.STATE_NONE:
			Log.debug(TAG, "Current state : NONE");
			mEtConsole.getText().append("State : NONE\n");
			setProgressBarIndeterminateVisibility(false);
			break;

		}

	}
}
