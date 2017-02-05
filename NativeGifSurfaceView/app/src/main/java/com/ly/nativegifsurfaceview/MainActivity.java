package com.ly.nativegifsurfaceview;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import pl.droidsonroids.gif.GifTextureView;
import pl.droidsonroids.gif.InputSource;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        GifTextureView gifTextureView = new GifTextureView(this);
        InputSource.FileSource fileSource = new InputSource.FileSource(Environment.getExternalStorageDirectory() + "/1.gif");
        gifTextureView.setInputSource(fileSource);
        setContentView(gifTextureView);

    }
}
