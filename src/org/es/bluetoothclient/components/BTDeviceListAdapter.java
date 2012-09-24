package org.es.bluetoothclient.components;

import java.util.List;

import org.es.bluetoothclient.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author Cyril Leroux
 *
 */
public class BTDeviceListAdapter extends BaseAdapter {

	List<BTDeviceInfo> mDeviceList;
	private final LayoutInflater mInflater;

	/**
	 * Adapter constructor
	 * @param context
	 * @param deviceList
	 */
	public BTDeviceListAdapter(final Context context, final List<BTDeviceInfo> deviceList) {
		mInflater = LayoutInflater.from(context);
		mDeviceList = deviceList;
	}

	@Override
	public int getCount() {
		if (mDeviceList == null) {
			return 0;
		}
		return mDeviceList.size();
	}

	@Override
	public Object getItem(int position) {
		if (mDeviceList == null) {
			return null;
		}
		return mDeviceList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/** Device information view */
	public static class ViewHolder {
		TextView tvDeviceName;
		TextView tvDeviceAddress;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.device_item, null);
			holder = new ViewHolder();
			holder.tvDeviceName = (TextView) convertView.findViewById(R.id.tvDeviceName);
			holder.tvDeviceAddress = (TextView) convertView.findViewById(R.id.tvDeviceAddress);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		BTDeviceInfo device = mDeviceList.get(position);
		holder.tvDeviceName.setText(device.getName());
		holder.tvDeviceAddress.setText(device.getAddress());

		return convertView;
	}

}
