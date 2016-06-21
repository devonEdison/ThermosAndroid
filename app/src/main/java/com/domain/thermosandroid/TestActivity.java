package com.domain.thermosandroid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.idevicesinc.sweetblue.BleManager;
import com.idevicesinc.sweetblue.BleManagerConfig;
import com.idevicesinc.sweetblue.utils.BluetoothEnabler;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";
    String scanString = "scan";
    public BleManager m_bleManager;
//    SharedPreferences prefs;
    ListView mListView;
    ArrayList<String> mList;
    ArrayAdapter<String> arrayAdapter;
    ProgressBar mProgressBar;
    public static String EXTRA_MESSAGE="extramessage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TestActivity.this, DisplayMessageActivity1.class);
                intent.putExtra(EXTRA_MESSAGE, mList.get(0));
                startActivity(intent);
            }
        });
//        prefs = this.getSharedPreferences("ThermosWITS", Context.MODE_PRIVATE);
        mList = new ArrayList<String>();
        mList.clear();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

    }

    @Override
    protected void onStart() {
        mProgressBar.setVisibility(View.VISIBLE);
        // A ScanFilter decides whether a BleDevice instance will be created from a
        // BLE advertisement and passed to the DiscoveryListener implementation below.
        final BleManagerConfig.ScanFilter scanFilter = new BleManagerConfig.ScanFilter() {
            @Override
            public Please onEvent(BleManagerConfig.ScanFilter.ScanEvent e) {
                return BleManagerConfig.ScanFilter.Please.acknowledgeIf(e.name_normalized().contains("thermos"));
//                        .thenStopScan();
                //thermos_34C7
                //thermos_053B
            }
        };


        // New BleDevice instances are provided through this listener.
        // Nested listeners then listen for connection and read results.
        // Obviously you will want to structure your actual code a little better.
        // The deep nesting simply demonstrates the async-callback-based nature of the API.
        final BleManager.DiscoveryListener discoveryListener = new BleManager.DiscoveryListener() {
            @Override
            public void onEvent(DiscoveryEvent e) {
//                Log.i(TAG, "onEvent A =  " + e);
                Log.i(TAG, "onEvent Aname =  " + e.device().getName_debug());

                mList.add(e.device().getName_debug());

//              if( e.was(BleManager.DiscoveryListener.LifeCycle.DISCOVERED) ) {
//                  e.device().connect(new BleDevice.StateListener() {
//                      @Override
//                      public void onEvent(StateEvent e) {
//                          Log.i(TAG, "connect =  " + e);
//                          scanString = scanString + "connect: " + e +" \n\n\n\n";
//                          scanText.setText(scanString);
//
//                          if( e.didEnter(BleDeviceState.INITIALIZED) ) {
//                              e.device().read(Uuids.BATTERY_LEVEL, new BleDevice.ReadWriteListener() {
//                                  @Override
//                                  public void onEvent(ReadWriteEvent e) {
//                                      if(e.wasSuccess()){
//                                          Log.i("", "Battery level is " + e.data_byte() + "%");
//                                          scanString = scanString + "readbattery state: " + e.data_byte() + "%" +" \n";
//                                          scanText.setText(scanString);
//                                      }
//                                  }
//                              });
//                          }
//                      }
//                  });
//              }
            }
        };

        // This class helps you navigate the treacherous waters of Android M Location requirements for scanning.
        // First it enables bluetooth itself, then location permissions, then location services. The latter two
        // are only needed in Android M.
        BluetoothEnabler.start(this, new BluetoothEnabler.DefaultBluetoothEnablerFilter() {
            @Override
            public Please onEvent(BluetoothEnabler.BluetoothEnablerFilter.BluetoothEnablerEvent e) {
                if (e.isDone()) {
                    e.bleManager().startScan(scanFilter, discoveryListener);
                    m_bleManager = e.bleManager();
                }

                //stop at 10 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        m_bleManager.stopScan();
                        arrayAdapter = new ArrayAdapter<String>
                                (TestActivity.this, android.R.layout.simple_list_item_1 , mList);
                        for (int i=0; i<mList.size(); i++)
                            Log.v(TAG, "check mList is " +mList.get(i));
                        mListView.setAdapter(arrayAdapter);
                        mProgressBar.setVisibility(View.GONE);
                    }
                }, 10000);

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
