package com.lak.tunebreaddriver;
//package com.example.userapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lak.tunebreaddriver.Util.AppConfig;

import java.io.File;
import java.io.FileOutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQRCode extends AppCompatActivity {
    Button generateBtn;
    ImageView qrImage;
    String data;
    private Bitmap bitmap;
    AppConfig appConfig;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qrcode);
        appConfig = new AppConfig(this);
        generateBtn = (Button) findViewById(R.id.generatebtn);
        qrImage = (ImageView) findViewById(R.id.imageQR);
        data = appConfig.getLogedUserID();
        QRGEncoder qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT,100);
        ImageView imgback = (ImageView)findViewById(R.id.img_back);
        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
        ActivityCompat.requestPermissions(GenerateQRCode.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(GenerateQRCode.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()+ "/Camera/Your_Directory_Name";
                File myDir = new File(root);
                myDir.mkdirs();
                String fname = "Image-" + data + ".png";
                File file = new File(myDir, fname);
                System.out.println(file.getAbsolutePath());
                if (file.exists()) file.delete();
                Log.i("LOAD", root + fname);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                MediaScannerConnection.scanFile(getBaseContext(), new String[]{file.getPath()}, new String[]{"image/jpeg"}, null);
                //  GenerateQRCode.this.startActivity(new Intent(GenerateQRCode.this, MapsActivity.class));
                Toast.makeText(GenerateQRCode.this, "QR Code Save to Storage Successfully", Toast.LENGTH_SHORT).show();
                ((Activity) GenerateQRCode.this).finish();

            }
        });
    }
}