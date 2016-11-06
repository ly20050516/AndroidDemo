package com.ly.gif;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ly.widget.GifSurfaceView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
//        final GifMovieView gif1 = (GifMovieView) findViewById(R.id.gif1);
//        gif1.setMovieResource(R.drawable.gif1);
        GifSurfaceView gifSurfaceView = (GifSurfaceView) findViewById(R.id.gif1);
        gifSurfaceView.setMovieResource(R.drawable.gif1);

        Intent intent = new Intent();
        intent.setClass(this,MainActivity2.class);
        startActivity(intent);
    }

    public void onGifClick(View v) {
//        GifMovieView gif = (GifMovieView) v;
//        gif.setPaused(!gif.isPaused());
        GifSurfaceView gifSurfaceView = (GifSurfaceView) v;
        gifSurfaceView.setPaused(!gifSurfaceView.isPaused());
    }
}
