package com.foxslip.faisal.bluechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        public boolean handleMessage(Message msg) {

            switch (msg.what)
            {
                case MESSAGE_STATE_CHANGED:
                    switch (msg.arg1)
                    {
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
                            setState("Connected: "+connectedDevice);
                            break;
                    }
                    break;
                case MESSAGE_READ:
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_DEVICE_NAME:
                    connectedDevice = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(),connectedDevice,Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(),msg.getData().getString(TOAST),Toast.LENGTH_LONG);
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


        messages.add(new ChatMessage("23","10:20","Hey man whats up",true));
        messages.add(new ChatMessage("23","10:20","Hey long time no see",false));
        messages.add(new ChatMessage("23","10:20","yeah we live the life of the mind now",true));
        messages.add(new ChatMessage("23","10:20","Yolo swag",true));
        messages.add(new ChatMessage("23","10:20","shut up",true));

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
            chatUtils.connect(bluetoothAdapter.getRemoteDevice(address));
            Toast.makeText(this,address,Toast.LENGTH_SHORT).show();
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
        messages.add(new ChatMessage("23","10:20",message_text_view.getText().toString(),true));
        message_text_view.setText("");
        conversationAdapter.notifyDataSetChanged();
        chatbox.scrollToPosition(conversationAdapter.getItemCount() - 1);
    }
}
