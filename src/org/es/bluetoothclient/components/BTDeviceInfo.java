package org.es.bluetoothclient.components;

import android.bluetooth.BluetoothDevice;

/**
 * Simple object that hold the bluetooth device informations.
 * @author Cyril Leroux
 *
 */
public class BTDeviceInfo {

	/**
	 * 
	 */
	public static final String TYPE_PAIRED = "Paired";
	/**
	 * 
	 */
	public static final String TYPE_NEW = "New";

	private final String mName;
	private final String mAddress;
	private final String mType;

	/**
	 * Constructor
	 * @param device
	 * @param type
	 */
	public BTDeviceInfo(BluetoothDevice device, String type) {
		mName		= device.getName();
		mAddress	= device.getAddress();
		mType		= type;
	}

	/**
	 * @return the name of the device
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return mAddress;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return mType;
	}
}
