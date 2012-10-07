package org.es.bluetoothclient.utils;

/**
 * Contains the intent keys.
 * @author Cyril Leroux
 *
 */
public class IntentKey {

	/** Device Address key. */
	public static String EXTRA_DEVICE_ADDRESS	= "device_address";
	// Key names received from the BluetoothChatService Handler
	public static final String DEVICE_NAME		= "device_name";
	public static final String TOAST			= "toast";

	// Message types sent from the BluetoothChatService Handler
	public static final int MESSAGE_STATE_CHANGE	= 1;
	public static final int MESSAGE_READ			= 2;
	public static final int MESSAGE_WRITE			= 3;
	public static final int MESSAGE_DEVICE_NAME		= 4;
	public static final int MESSAGE_TOAST			= 5;
}
