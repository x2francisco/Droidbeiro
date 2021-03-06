package pt.up.fe.droidbeiro.Service.BLE;

/**
 * Created by Diogo on 04/12/2014.
 */


import android.app.Service;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import java.util.UUID;

public class DeviceControlService extends Service {

    public static final String BROADCAST_ACTION = "com.example.bluetooth.le.CONTROL_SERVICE_CONNECTED";
    public static final String HR_DATA = "com.example.bluetooth.le.HR_DATA";

    private final static String TAG = DeviceControlService.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.v(TAG,"Connected");

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.v(TAG,"Disconnected");

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                //Call the supported services and characteristics on the user interface.
                getFeatures();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                //displayDataBat(intent.getStringExtra(BluetoothLeService.EXTRA_DATABAT));
            }
        }
    };

    //More info @ http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete

    private void getFeatures() {

        //Battery Service
        final BluetoothGattService BatteryService = mBluetoothLeService.
                getGattServices(UUID.fromString(SampleGattAttributes.BATTERY_SERVICE));

        final BluetoothGattCharacteristic batteryLevel = BatteryService.
                getCharacteristic(UUID.fromString(SampleGattAttributes.BATTERY_LEVEL));

        //Heart Rate Service
        final BluetoothGattService HeartRateService = mBluetoothLeService.
                getGattServices(UUID.fromString(SampleGattAttributes.HEART_RATE_SERVICE));

        final BluetoothGattCharacteristic HeartRateLevel = HeartRateService.
                getCharacteristic(UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT));

        final int charaPropBat = batteryLevel.getProperties();
        final int charaPropHR = HeartRateLevel.getProperties();

        //readGATTCharacteristic(charaPropBat, batteryLevel);
        readGATTCharacteristic(charaPropHR, HeartRateLevel);

    }

    private void readGATTCharacteristic(int charaProp, BluetoothGattCharacteristic characteristic){

        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            // If there is an active notification on a characteristic, clear
            // it first so it doesn't update the data field on the user interface.
            if (mNotifyCharacteristic != null) {
                mBluetoothLeService.setCharacteristicNotification(
                        mNotifyCharacteristic, false);
                mNotifyCharacteristic = null;
            }
            mBluetoothLeService.readCharacteristic(characteristic);
        }
        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            mNotifyCharacteristic = characteristic;
            mBluetoothLeService.setCharacteristicNotification(
                    characteristic, true);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        //Intent broadCastIntent = new Intent(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up Log messages.

        Log.v(TAG, "Device Name: "+mDeviceName);
        Log.v(TAG, "Device Address: "+mDeviceAddress);

        return 1;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void displayData(String data) {
        if (data != null) {
            broadcastUpdate(BROADCAST_ACTION,data);
            //Log.v(TAG,"Heart Rate Data: "+data);
        }
    }

    private void broadcastUpdate(final String action, final String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(HR_DATA, data);
        sendBroadcast(intent);
    }

    /*private void displayDataBat(String data) {
        if (data != null) {
            mDataBattery.setText(data);}
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
}
