package com.foxslip.faisal.bluechat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class device_list extends AppCompatActivity {

    private ListView listPairedDevices, listAvailableDevices;

    ProgressBar scanProgressBar;

    private ArrayAdapter<String> adapterPairedDevices, adapterAvailableDevices;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        context = this;

        init();
    }


    public void cancelActivity(View view) {

        Intent intent = new Intent(this, MainActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void init()
    {
        scanProgressBar = (ProgressBar)findViewById(R.id.progressbar_scan);

        listPairedDevices = (ListView)findViewById(R.id.paired_devices);
        listAvailableDevices = (ListView)findViewById(R.id.available_devices);

        adapterPairedDevices = new ArrayAdapter<String>(context,R.layout.device_list_item);
        adapterAvailableDevices = new ArrayAdapter<String>(context,R.layout.device_list_item);

        listPairedDevices.setAdapter(adapterPairedDevices);
        listAvailableDevices.setAdapter(adapterAvailableDevices);

        listPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView)view).getText().toString();
                String address = info.substring(info.length() - 17);

                Intent intent = new Intent();
                intent.putExtra("deviceAddress",address);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        listAvailableDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView)view).getText().toString();
                String address = info.substring(info.length() - 17);

                Intent intent = new Intent();
                intent.putExtra("deviceAddress",address);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        startScan();
        Log.d("This ", "init: "+pairedDevices.size());

        if (pairedDevices != null && pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                adapterPairedDevices.add(device.getName() + "\n" +device.getAddress());
            }
        }

        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothDeviceListner,intentFilter);
        IntentFilter intentFilter1 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothDeviceListner,intentFilter1);
    }

    private BroadcastReceiver bluetoothDeviceListner = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED)
                {
                    adapterAvailableDevices.add(device.getName()+"\n"+device.getAddress());
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                scanProgressBar.setVisibility(View.GONE);
                if (adapterAvailableDevices.getCount() == 0)
                {
                    Toast.makeText(context,"No devices found",Toast.LENGTH_LONG).show();
                }
                else
                    {
                        Toast.makeText(context,"Click on the device to start communicating",Toast.LENGTH_SHORT).show();
                    }
            }
        }
    };

    @Override
    protected void onDestroy() {
        try{
            if(bluetoothDeviceListner!=null)
                unregisterReceiver(bluetoothDeviceListner);

        }catch(Exception e){}
        super.onDestroy();
    }

    public void onPause() {
        try{
            if(bluetoothDeviceListner!=null)
                unregisterReceiver(bluetoothDeviceListner);

        }catch(Exception e){}
        super.onPause();
    }

    public void scanDevices(View view) {
        startScan();
    }

    private void startScan()
    {
        scanProgressBar.setVisibility(View.VISIBLE);
        adapterAvailableDevices.clear();

        Toast.makeText(context,"Scan Started",Toast.LENGTH_LONG).show();

        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
    }

    public void waitForConnection(View view) {

        String info = ((TextView)view).getText().toString();
        String address = info.substring(info.length() - 17);
        Intent intent = new Intent();
        intent.putExtra("deviceAddress","wait");
        setResult(RESULT_OK,intent);
        finish();

    }
}
