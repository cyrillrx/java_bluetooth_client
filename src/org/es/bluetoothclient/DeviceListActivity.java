package org.es.bluetoothclient;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.view.Window.FEATURE_INDETERMINATE_PROGRESS;
import static org.es.bluetoothclient.BuildConfig.DEBUG;
import static org.es.bluetoothclient.components.BTDevice.TYPE_NEW;
import static org.es.bluetoothclient.components.BTDevice.TYPE_PAIRED;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.es.bluetoothclient.components.BTDevice;
import org.es.bluetoothclient.components.DeviceListAdapter;
import org.es.bluetoothclient.utils.IntentKey;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * This activity displays the list of available bluetooth devices.
 * 
 * @author Cyril Leroux
 *
 */
public class DeviceListActivity extends ListActivity {

	private static final String TAG = "DeviceListActivity";

	private BluetoothAdapter mBluetoothAdapter;
	private List<BTDevice> mDeviceList;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if ( DEBUG) {
					Log.d(TAG, "Discovered " + device.getName());
				}

				mDeviceList.add(new BTDevice(device, TYPE_NEW));
				updateView();

			} else if (ACTION_DISCOVERY_FINISHED.equals(action)) {
				if ( DEBUG) {
					Log.d(TAG, "Discovery is finished");
				}
				setProgressBarIndeterminateVisibility(false);
				updateView();

			} else {
				if ( DEBUG) {
					Log.d(TAG, "Action : " + action);
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_device_list);

		// Register for broadcasts when a device is discovered
		registerReceiver(mReceiver, new IntentFilter(ACTION_FOUND));
		registerReceiver(mReceiver, new IntentFilter(ACTION_DISCOVERY_FINISHED));

		if (mDeviceList == null) {
			mDeviceList = new ArrayList<BTDevice>();
		}

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			//			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mDeviceList.add(new BTDevice(device, TYPE_PAIRED));
			}
		} else {
			//			String noDevices = getResources().getText(R.string.none_paired).toString();
			//			mPairedDevicesArrayAdapter.add(noDevices);
		}

		discoverDevices();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
		}

		unregisterReceiver(mReceiver);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_device_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miRefreshBTDevices:
			discoverDevices();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Search for devices.
	 */
	private void discoverDevices() {
		setProgressBarIndeterminateVisibility(true);

		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}

		mBluetoothAdapter.startDiscovery();
	}

	private void updateView() {

		DeviceListAdapter adapter = new DeviceListAdapter(getApplicationContext(), mDeviceList);
		setListAdapter(adapter);
		ListView listView = getListView();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				mBluetoothAdapter.cancelDiscovery();

				BTDevice device = mDeviceList.get(position);
				Intent intent = new Intent();
				intent.putExtra(IntentKey.EXTRA_DEVICE_ADDRESS, device.getAddress());

				// Set result and finish this Activity
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

}
