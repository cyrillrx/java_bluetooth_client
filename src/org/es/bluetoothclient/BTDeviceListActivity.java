package org.es.bluetoothclient;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothDevice.ACTION_FOUND;
import static android.view.Window.FEATURE_INDETERMINATE_PROGRESS;
import static org.es.bluetoothclient.BuildConfig.DEBUG;
import static org.es.bluetoothclient.components.BTDeviceInfo.TYPE_NEW;
import static org.es.bluetoothclient.components.BTDeviceInfo.TYPE_PAIRED;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.es.bluetoothclient.components.BTDeviceInfo;
import org.es.bluetoothclient.components.BTDeviceListAdapter;
import org.es.bluetoothclient.utils.IntentKey;

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
public class BTDeviceListActivity extends ListActivity {

	private static final String TAG = "BTDeviceListActivity";

	private BluetoothAdapter mBluetoothAdapter;
	private List<BTDeviceInfo> mBondedDeviceList;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();

			if (ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if ( DEBUG) {
					Log.d(TAG, "Discovered " + device.getName());
				}
				mBondedDeviceList.add(new BTDeviceInfo(device, TYPE_NEW));

			} else if (ACTION_DISCOVERY_FINISHED.equals(action)) {
				if ( DEBUG) {
					Log.d(TAG, "ACTION_DISCOVERY_FINISHED");
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

		if (mBondedDeviceList == null) {
			mBondedDeviceList = new ArrayList<BTDeviceInfo>();
		}

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		// If there are paired devices, add each one to the ArrayAdapter
		if (pairedDevices.size() > 0) {
			//			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				mBondedDeviceList.add(new BTDeviceInfo(device, TYPE_PAIRED));
			}
		} else {
			//			String noDevices = getResources().getText(R.string.none_paired).toString();
			//			mPairedDevicesArrayAdapter.add(noDevices);
		}
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

		BTDeviceListAdapter adapter = new BTDeviceListAdapter(getApplicationContext(), mBondedDeviceList);
		setListAdapter(adapter);
		ListView listView = getListView();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}
				BTDeviceInfo device = mBondedDeviceList.get(position);
				Intent intent = new Intent();
				intent.putExtra(IntentKey.EXTRA_DEVICE_ADDRESS, device.getAddress());
			}
		});
	}

}
