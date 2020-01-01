package com.example.p2pdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.telecom.TelecomManager;
import android.widget.Toast;

public class WiFiDirectBroatcastReeeiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private MainActivity mActivity;
    private WifiP2pManager.Channel mChannel;


    public WiFiDirectBroatcastReeeiver(WifiP2pManager mManager, MainActivity mActivity, WifiP2pManager.Channel mChannel) {
        this.mManager = mManager;
        this.mActivity = mActivity;
        this.mChannel = mChannel;
    }




    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)){
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE,-1);

            if(state==WifiP2pManager.WIFI_P2P_STATE_ENABLED){
                Toast.makeText(context,"Wifi is ON",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Wifi is OFF",Toast.LENGTH_LONG).show();
            }

        }else if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            if(mManager!=null){
                mManager.requestPeers(mChannel,mActivity.peerListListener);
            }

        }else if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){

        }else if(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)){

        }

    }
}
