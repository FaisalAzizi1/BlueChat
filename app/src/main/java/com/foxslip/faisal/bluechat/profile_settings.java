package com.foxslip.faisal.bluechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class profile_settings extends AppCompatActivity {

    private static int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private EditText nameText;

    String SHARED_PREFS = "codeTheme";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);



        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ImageView imageSelect = (ImageView) findViewById(R.id.profile_image_settings);
        nameText = (EditText)findViewById(R.id.username);
        nameText.setHint("USERNAME");
        if (sharedPreferences != null)
            nameText.setText(sharedPreferences.getString("username",null));

        loadImageFromStorage("/data/user/0/com.foxslip.faisal.bluechat/app_imageDir/");
        imageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = findViewById(R.id.profile_image_settings);
                imageView.setImageBitmap(bitmap);
                this.bitmap = bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", this.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");
        Log.d("PATHH", "saveToInternalStorage: "+mypath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void loadImageFromStorage(String path)
    {
        try {

            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.profile_image_settings);
            img.setImageBitmap(b);
            bitmap = b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public void saveProfile(View view) {

        saveToInternalStorage(bitmap);
        SharedPreferences sharedPreferences = getSharedPreferences("username", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        BluetoothAdapter bluetoothAdapter = null;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!nameText.getText().toString().equals("")){
            editor.putString("username", nameText.getText().toString());

            if (bluetoothAdapter != null)
                bluetoothAdapter.setName(nameText.getText().toString());
            editor.commit();
        }

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
