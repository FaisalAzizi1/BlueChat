package com.foxslip.faisal.bluechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Chat extends AppCompatActivity {

    private ChatUtils chatUtils;

    private BluetoothAdapter bluetoothAdapter;
    private RecyclerView chatbox;
    private ConversationAdapter conversationAdapter;

    private final int SELECT_DEVICE = 102;
    private TextView status;
    private EditText message_text_view;

    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;

    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";

    List<ChatMessage> messages = new ArrayList<>();

    private String connectedDevice;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case ChatUtils.STATE_NONE:
                            setState("Not Connected");
                            break;
                        case ChatUtils.STATE_LISTEN:
                            setState("Not Connected");
                            break;
                        case ChatUtils.STATE_CONNECTING:
                            setState("Connecting...");
                            break;
                        case ChatUtils.STATE_CONNECTED:
                            chatUtils.write("Faisal Azizi".getBytes());
                            setState("Connected: " + connectedDevice);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    messages.add(new ChatMessage("","12:00",outputBuffer,true));
                    Log.d("MESSAGE", "handleMessage: "+outputBuffer);
                    conversationAdapter.notifyDataSetChanged();
                    chatbox.scrollToPosition(conversationAdapter.getItemCount() - 1);
                    break;
                case MESSAGE_READ:
                    byte[] buffer = (byte[]) message.obj;
                    String inputBuffer = new String(buffer, 0, message.arg1);
                    messages.add(new ChatMessage("","12:00",inputBuffer,false));
                    Log.d("MESSAGE", "handleMessage: INPUT"+inputBuffer);
                    conversationAdapter.notifyDataSetChanged();
                    chatbox.scrollToPosition(conversationAdapter.getItemCount() - 1);
                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = message.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), connectedDevice, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void setState(CharSequence subtitle)
    {
        status.setText(subtitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        status = (TextView)findViewById(R.id.status);
        chatUtils = new ChatUtils(this,handler);
    }



    private void init()
    {
        chatbox = (RecyclerView) findViewById(R.id.chat_box);
        conversationAdapter = new ConversationAdapter(this,messages);
        chatbox.setLayoutManager(new LinearLayoutManager(this));
        chatbox.setAdapter(conversationAdapter);
        message_text_view = (EditText)findViewById(R.id.message_box_message);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Intent intent = new Intent(this,device_list.class);
        startActivityForResult(intent,SELECT_DEVICE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == SELECT_DEVICE && resultCode == RESULT_OK)
        {
            String address = data.getStringExtra("deviceAddress");
            Log.d("waiting", "onActivityResult: "+address);

            if (!address.equals("wait"))
                chatUtils.connect(bluetoothAdapter.getRemoteDevice(address));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (chatUtils!=null){
            chatUtils.stop();
        }
    }

    public void sendMessage(View view) {
        String message = message_text_view.getText().toString();
        if (!message.isEmpty()) {
            message_text_view.setText("");
            chatUtils.write(message.getBytes());
            conversationAdapter.notifyDataSetChanged();
        }
        chatbox.scrollToPosition(conversationAdapter.getItemCount() - 1);
    }

    public void closeChat(View view) {

        //super.onDestroy();
        if (chatUtils!=null){
            chatUtils.stop();
        }

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        if (messages.size() > 0){
        Conversation conversation = new Conversation(new Random(1000).toString(),currentDateTimeString,messages,"Faisal",connectedDevice);
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.addConveration(conversation);

        }

        Intent intent = new Intent(this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
