package com.example.videoview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mvideoview.IMediaPlayer;
import com.example.mvideoview.MyVideoView;


public class MainActivity extends AppCompatActivity {
    private MyVideoView myVideoView;
    /**
     * 视频文件路径
     */
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myVideoView = findViewById(R.id.videoview);
        myVideoView.setmOnProgressListener(new IMediaPlayer.OnProgressListener() {
            @Override
            public void onSurfaceCreated() {
                //设置播放路径
                myVideoView.setVideoURL(url);
            }

            @Override
            public void onPreparedListener(IMediaPlayer mediaPlayer) {
                //开始播放
                mediaPlayer.start();
            }

            @Override
            public void onTotleProgressListener(int totalProgress) {
                //拿到总进度值，可以在这里设置seekbar的Max值
            }

            @Override
            public void onPlayProgress(int progress) {
                //进度播放回调，一秒一次
            }

            @Override
            public void onCompletion() {
                //播放完成时
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        url = "http://10.0.0.62:8999/movie/284b089e0be02011e70bb050484d7ebf82e4.mp4";
    }
}
