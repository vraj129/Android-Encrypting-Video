package com.example.offlinefeature;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


public class MainActivity extends AppCompatActivity {
    private Button btn,btn1,btn2;
    boolean isDownloaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String url = "https://i.imgur.com/7bMqysJ.mp4";
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        btn = findViewById(R.id.button);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        VideoView videoView = findViewById(R.id.videoview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    String title = URLUtil.guessFileName(url,null,null);
                    request.setTitle(title);
                    String cookie = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie",cookie);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title);
                    downloadManager.enqueue(request);
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = URLUtil.guessFileName(url,null,null);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File plain = new File(path,title);

                File eplain = new File(path,title+"encrypted.txt");
                try {
                    DES.encryptDecrypt("12345678", Cipher.ENCRYPT_MODE,plain,eplain);
                    plain.delete();
                } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = URLUtil.guessFileName(url,null,null);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File eplain = new File(path,title+"encrypted.txt");
                //File dec = new File(path,title+"dec.mp4");

                try {
                    File tempVideo = File.createTempFile("test","mp4");
                    tempVideo.deleteOnExit();
                    DES.encryptDecrypt("12345678",Cipher.DECRYPT_MODE,eplain,tempVideo);
                    Uri uri = Uri.parse(tempVideo.getAbsolutePath());
                    videoView.setVideoURI(uri);
                    MediaController mediaController = new MediaController(MainActivity.this);
                    videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(videoView);
                } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

