package com.foxslip.faisal.bluechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context mConext;
    private BluetoothAdapter bluetoothAdapter;

    private final int LOCATION_PERMISSION_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConext = this;

        initBluetooth();
        Intent intent = new Intent(mConext,DeviceList.class);
        startActivity(intent);
    }

    public void initBluetooth()
    {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null)
            Toast.makeText(mConext,"Bluetooth not found",Toast.LENGTH_LONG).show();
    }

    private void checkPermissions()
    {
        if (ContextCompat.checkSelfPermission(mConext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent intent = new Intent(mConext,DeviceList.class);
                startActivity(intent);
            }
            else
                {
                    new AlertDialog.Builder(mConext)
                            .setCancelable(false)
                            .setMessage("Location Permision is Required")
                            .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    checkPermissions();
                                }
                            })
                            .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    MainActivity.this.finish();
                                }
                            })
                            .show();
                }
        }else{
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void enableBluetooth(View view)
    {
        if (bluetoothAdapter.isEnabled())
        {
            Toast.makeText(mConext,"Already enabled",Toast.LENGTH_LONG).show();
            checkPermissions();
        }
        else
            {
                bluetoothAdapter.enable();
            }
    }
}
