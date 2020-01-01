package com.example.p2pdemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.aware.WifiAwareManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

TextView txtConStatus;
Button btnOnOff, btnDiscover;
ListView listView;


WifiManager wifiManager;
WifiP2pManager mManager;
WifiP2pManager.Channel mChannel;

BroadcastReceiver mReceiver;
IntentFilter mIntentFilter;

List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
String [] deviceNameArray;
WifiP2pDevice[] deviceArray;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        khoiTao();
        btnClick();





    }

    private void khoiTao(){
        txtConStatus = findViewById(R.id.textView_ConnectStatus);
        btnOnOff = findViewById(R.id.button_OnOff);
        btnDiscover = findViewById(R.id.button_Discover);
        listView = findViewById(R.id.listView);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()){
            btnOnOff.setText("OFF");
        }else {
            btnOnOff.setText("on");
        }

        mManager = (WifiP2pManager) getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this,getMainLooper(),null);

        mReceiver = new WiFiDirectBroatcastReeeiver(mManager,this,mChannel);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }

    private void btnClick(){
        btnOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(wifiManager.isWifiEnabled()){
                    wifiManager.setWifiEnabled(false);
                    btnOnOff.setText("ON");
                }else {
                    wifiManager.setWifiEnabled(true);
                    btnOnOff.setText("OFF");
                }
            }
        });

        btnDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        txtConStatus.setText("Discovery Started");
                    }

                    @Override
                    public void onFailure(int reason) {
                        txtConStatus.setText("Discovery Startting Failed");
                    }
                });
            }
        });
    }

    WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peersList) {
            Log.d("DDDDĐ", String.valueOf(peersList.getDeviceList().size()));
            if(!peersList.getDeviceList().equals(peers)){

                peers.clear();
                peers.addAll(peersList.getDeviceList());

                deviceNameArray = new String[peersList.getDeviceList().size()];
                deviceArray = new WifiP2pDevice[peersList.getDeviceList().size()];

                int index =0;
                for(WifiP2pDevice device : peersList.getDeviceList()){
                    deviceNameArray[index]= device.deviceName;
                    deviceArray[index]= device;
                    index++;
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_expandable_list_item_1,deviceNameArray);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                txtConStatus.setText(deviceNameArray[0]);
            }
            if(peers.size()==0){
                Toast.makeText(getApplicationContext(),"No Device Found",Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver,mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
}
