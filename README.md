# VideoView
自封装MediaPlayer+SurfaceVIew 和TextureView 播放控件

使用方法：
xml文件内添加
```
<com.example.mvideoview.MyVideoView
        android:id="@+id/videoview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
或者
```
<com.example.mvideoview.MyTextureVideoView
        android:id="@+id/videoview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

在界面内设置
```
//播放路径
String url = "";
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
```