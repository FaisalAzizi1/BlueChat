package com.foxslip.faisal.bluechat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {

    private Context mConext;
    private BluetoothAdapter bluetoothAdapter;
    private HistoryAdapter historyAdapter;
    private RecyclerView conversations_view;

    private List<Conversation> conversations = new ArrayList<>();

    //Location permision request contant
    private final int LOCATION_PERMISSION_REQUEST = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mConext = this;

        init();
        initBluetooth();

    }

    private void init()
    {
        loadImageFromStorage("/data/user/0/com.foxslip.faisal.bluechat/app_imageDir/");
        DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());

        List<ChatMessage> chatMessages= new ArrayList<ChatMessage>();
        chatMessages.add(new ChatMessage("","12:32","hello",false));
        chatMessages.add(new ChatMessage("","12:32","hello",true));
        chatMessages.add(new ChatMessage("","12:32","hello",false));
        chatMessages.add(new ChatMessage("","12:32","Hey my man",true));

        Conversation conversation = new Conversation("452","12:12:12",chatMessages,"Faisal","Robina");

         //
        // databaseHandler.addConveration(conversation);
         //Conversation conversation1 = databaseHandler.getConversation("1234");
       // Log.d("TAAG", "enableBluetooth: "+conversation1.getConversation().get(2).getTime());
        conversations = databaseHandler.getAllConversations();


        conversations_view = (RecyclerView)findViewById(R.id.conversations_view);
        historyAdapter = new HistoryAdapter(conversations,this);
        conversations_view.setLayoutManager(new LinearLayoutManager(this));
        conversations_view.setAdapter(historyAdapter);

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
                Intent intent = new Intent(mConext,Chat.class);
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

    public void enableBluetooth(View view) {


        if (bluetoothAdapter != null){
        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(mConext, "Already enabled", Toast.LENGTH_LONG).show();
            checkPermissions();
            Intent intent = new Intent(mConext,Chat.class);
            startActivity(intent);
        } else {
            bluetoothAdapter.enable();
        }

        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)
        {
            Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(discoveryIntent);
        }
        }
        else
            {
                Toast.makeText(this,"Bluetooth Services Not Available",Toast.LENGTH_SHORT).show();
            }

    }
    public void goToSettings(View view) {
        Intent intent = new Intent(this,profile_settings.class);
        startActivity(intent);
        finish();
    }

    private void loadImageFromStorage(String path)
    {

        try {

            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.profile_image_settings);
            img.setImageBitmap(b);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
