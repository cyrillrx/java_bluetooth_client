package org.es.bluetoothclient;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * This activity displays the list of available bluetooth devices.
 * 
 * @author Cyril Leroux
 *
 */
public class BTDeviceListActivity extends ListActivity {

	private static final String TAG = "BTDeviceListActivity";

	private BluetoothAdapter mBluetoothAdapter;
	private ArrayAdapter<String> mPairedDevices;
	private ArrayAdapter<String> mNewDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_list);

	}

}
