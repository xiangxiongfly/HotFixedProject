package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        findViewById(R.id.perform).setOnClickListener(v -> {
            sayHello();
        });

        findViewById(R.id.fixed).setOnClickListener(v -> {
            fixed();
        });
    }

    private void sayHello() {
        new User().sayHello();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);
    }

    private void fixed() {
        moveFile();
    }

    /**
     * 移动文件，将dex文件移动到私有目录下
     */
    private void moveFile() {
        File srcDir = getDir("fixed_dir", Context.MODE_PRIVATE);
        String fileName = "fixed-01.dex";
        File srcFile = new File(srcDir, fileName);
        if (!srcFile.exists()) {
            srcFile.delete();
        }

        File targetFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "fixed-01.dex");
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(targetFile);
            fileOutputStream = new FileOutputStream(srcFile);
            int len = 0;
            byte[] buffer = new byte[2048];
            while ((len = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                fileOutputStream.flush();
            }

            FixMamager.loadFixDex(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
}