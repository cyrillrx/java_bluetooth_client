package org.es.bluetoothclient;

import org.es.bluetoothclient.components.BluetoothService;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * Main activity of the BluetoothClient application.
 * The purpose of the application is to send data through bluetooth.
 * 
 * @author Cyril Leroux
 *
 */
public class MainActivity extends Activity implements OnClickListener {

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE_SECURE		= 0;
	private static final int REQUEST_CONNECT_DEVICE_INSECURE	= 1;
	private static final int REQUEST_ENABLE_BT					= 2;

	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothService mBluetoothService;
	private StringBuffer mOutStringBuffer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onStart() {
		super.onStart();

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mBluetoothService == null) {
				initBluetoothService();
			}
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
			startActivity(new Intent(this, DeviceListActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSend:
			// TODO send an event through bluetooth.
			break;

		default:
			break;
		}

	}

	private void initBluetoothService() {

	}

	/**
	 * Sends a message.
	 * @param message The message to send over bluetooth.
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		if (mBluetoothService.getState() != BluetoothService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}
		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the BluetoothChatService to write
			byte[] send = message.getBytes();
			mBluetoothService.write(send);

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			//mOutEditText.setText(mOutStringBuffer);
		}
	}
}
