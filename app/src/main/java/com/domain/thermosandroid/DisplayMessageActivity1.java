package com.domain.thermosandroid;

import android.content.Intent;
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

public class DisplayMessageActivity1 extends AppCompatActivity {

    private static final String TAG = "DisplayMessageActivity1";
    String scanString = "";
    TextView scanText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message1);

        Intent intent = getIntent();
        message = intent.getStringExtra(TestActivity.EXTRA_MESSAGE);
        Log.d(TAG,"message is = " + message);
        scanText = (TextView)findViewById(R.id.scanText);

    }
    String message;

    @Override
    protected void onStart() {
        super.onStart();
        final BleManagerConfig.ScanFilter scanFilter = new BleManagerConfig.ScanFilter() {
            @Override
            public Please onEvent(BleManagerConfig.ScanFilter.ScanEvent e) {
                return BleManagerConfig.ScanFilter.Please.acknowledgeIf(e.name_normalized().contains(message)).thenStopScan();

            }
        };

        final BleManager.DiscoveryListener discoveryListener = new BleManager.DiscoveryListener() {
            @Override
            public void onEvent(DiscoveryEvent e) {
//                Log.i(TAG, "onEvent A =  " + e);
                Log.i(TAG, "onEvent Aname =  " + e.device().getName_debug());
                Log.d(TAG,"33333333333");
              if( e.device().getName_debug().equals(message) ) {
                  m_bleManager.stopScan();
                  e.device().connect(new BleDevice.StateListener() {
                      @Override
                      public void onEvent(StateEvent e) {
                          Log.i(TAG, "connect =  " + e);
                          scanString = scanString + "connect: " + e +" \n\n\n\n";
                          scanText.setText(scanString);

                          if( e.didEnter(BleDeviceState.INITIALIZED) ) {
                              e.device().read(Uuids.BATTERY_LEVEL, new BleDevice.ReadWriteListener() {
                                  @Override
                                  public void onEvent(BleDevice.ReadWriteListener.ReadWriteEvent e) {
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

        BluetoothEnabler.start(this, new BluetoothEnabler.DefaultBluetoothEnablerFilter() {
            @Override
            public Please onEvent(BluetoothEnabler.BluetoothEnablerFilter.BluetoothEnablerEvent e) {
                Log.d(TAG,"1111111111");
                if (e.isDone()) {
                    Log.d(TAG,"22222222222");
                    e.bleManager().startScan(scanFilter, discoveryListener);
                    m_bleManager = e.bleManager();
                }
                return super.onEvent(e);
            }
        });
    }
    public BleManager m_bleManager;
}
