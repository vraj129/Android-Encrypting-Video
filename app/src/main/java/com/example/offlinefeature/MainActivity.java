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
import android.widget.Toast;
import android.widget.VideoView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;


public class MainActivity extends AppCompatActivity {
    private Button btn,btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidNetworking.initialize(getApplicationContext());
        String title="video.mp4";
        String url = "https://i.imgur.com/7bMqysJ.mp4";
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        btn = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        VideoView videoView = findViewById(R.id.videoview);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if(path.exists()){
                    path.delete();
                    AndroidNetworking.forceCancelAll();
                }
                AndroidNetworking.download(url,""+ path,title)
                        .setTag("downloadTest")
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .startDownload(new DownloadListener() {
                            @Override
                            public void onDownloadComplete() {
                                Toast.makeText(MainActivity.this,"Downloaded",Toast.LENGTH_SHORT).show();
                               // String title = URLUtil.guessFileName(url,null,null);
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

                            @Override
                            public void onError(ANError anError) {
                                Log.e("ERROR",anError.toString());
                            }
                        });
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File eplain = new File(path,title+"encrypted.txt");

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

