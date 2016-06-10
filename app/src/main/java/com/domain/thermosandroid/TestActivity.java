package com.domain.thermosandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.idevicesinc.sweetblue.BleDevice;
import com.idevicesinc.sweetblue.BleDeviceState;
import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerConfig;
import com.idevicesinc.sweetblue.utils.BluetoothEnabler;
import com.idevicesinc.sweetblue.utils.Uuids;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = "TestActivity";
    TextView scanText;
    String scanString="scan";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        scanText = (TextView) findViewById(R.id.textView2);
    }

    @Override
    protected void onStart() {
// A ScanFilter decides whether a BleDevice instance will be created from a
// BLE advertisement and passed to the DiscoveryListener implementation below.
        final BleManagerConfig.ScanFilter scanFilter = new BleManagerConfig.ScanFilter() {
            @Override public Please onEvent(BleManagerConfig.ScanFilter.ScanEvent e) {
                Log.i(TAG, "1111111111111111" + e);
                scanString = scanString + e + "\n\n\n\n\n";
                scanText.setText(scanString);
                return BleManagerConfig.ScanFilter.Please.acknowledgeIf(e.name_normalized().contains("thermos"))
                        .thenStopScan();
            }
        };


        // New BleDevice instances are provided through this listener.
        // Nested listeners then listen for connection and read results.
        // Obviously you will want to structure your actual code a little better.
        // The deep nesting simply demonstrates the async-callback-based nature of the API.
        final BleManager.DiscoveryListener discoveryListener = new BleManager.DiscoveryListener() {
            @Override public void onEvent(DiscoveryEvent e) {
                Log.i(TAG, "onEvent A =  " + e);
                scanString = scanString + "discover: " + e +" \n\n\n\n";
                scanText.setText(scanString);

                if( e.was(BleManager.DiscoveryListener.LifeCycle.DISCOVERED) ) {
                    e.device().connect(new BleDevice.StateListener() {
                        @Override
                        public void onEvent(StateEvent e) {
                            Log.i(TAG, "connect =  " + e);
                            scanString = scanString + "connect: " + e +" \n\n\n\n";
                            scanText.setText(scanString);

                            if( e.didEnter(BleDeviceState.INITIALIZED) ) {
                                e.device().read(Uuids.BATTERY_LEVEL, new BleDevice.ReadWriteListener() {
                                    @Override
                                    public void onEvent(ReadWriteEvent e) {
                                        if(e.wasSuccess()){
                                            Log.i("", "Battery level is " + e.data_byte() + "%");
                                            scanString = scanString + "readbattery state: " + e.data_byte() + "%" +" \n";
                                            scanText.setText(scanString);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        };

// This class helps you navigate the treacherous waters of Android M Location requirements for scanning.
// First it enables bluetooth itself, then location permissions, then location services. The latter two
// are only needed in Android M.
        BluetoothEnabler.start(this, new BluetoothEnabler.DefaultBluetoothEnablerFilter() {
            @Override public Please onEvent(BluetoothEnabler.BluetoothEnablerFilter.BluetoothEnablerEvent e) {
                if( e.isDone() ) {
                    e.bleManager().startScan(scanFilter, discoveryListener);
                }
                return super.onEvent(e);
            }
        });

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
