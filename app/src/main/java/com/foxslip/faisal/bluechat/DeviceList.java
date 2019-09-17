package com.foxslip.faisal.bluechat;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Set;

public class DeviceList extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        init();
    }


    private void init()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        textView = (TextView)findViewById(R.id.device);
        Set<BluetoothDevice> bluetoothDevice = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device: bluetoothDevice) {
            textView.setText(device.getName());
        }
    }
}
