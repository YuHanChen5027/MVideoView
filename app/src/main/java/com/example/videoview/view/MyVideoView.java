package com.example.videoview.view;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chenyuhan on 2017/11/28.
 */

public class MyVideoView extends SurfaceView implements IMediaPlayer {
    private final String TAG = MyVideoView.class.getSimpleName();
    private Context mContext;
    private MediaPlayer mMediaPlayer;
    private Uri mUri;

    /**
     * 当前控件状态
     */
    private int mCurrentState = STATE_IDLE;

    /**
     * 控件状态类型(依次是：错误，空闲，准备，准备完成，播放，暂停，播放完成)
     */
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    /**
     * 音量
     */
    private float volumeNumber = 1.0f;

    public float getVolumeNumber() {
        return volumeNumber;
    }

    public void setVolumeNumber(float volumeNumber) {
        this.volumeNumber = volumeNumber;
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volumeNumber, volumeNumber);
        }
    }

    /**
     * 播放进度监听
     */
    private IMediaPlayer.OnProgressListener mOnProgressListener;

    /**
     * 错误监听监听
     */
    private IMediaPlayer.OnErrorListener mOnErrorListener;

    /**
     * 事件发生监听
     */
    private IMediaPlayer.OnInfoListener mOnInfoListener;

    public MyVideoView(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }


    private void init() {
        if (mMediaPlayer == null) {
            initMediaPLayer();
        }
        this.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (mMediaPlayer != null) {
                    bindSurfaceHolder(mMediaPlayer, holder);
                } else {
                    openVideo();
                }
                mOnProgressListener.onSurfaceCreated();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    /**
     * 初始化mediaplayer
     */
    private void initMediaPLayer() {
        mMediaPlayer = new MediaPlayer();
        //设置视频加载完成回调
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        //播放完成回调
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        //播放错误回调
        mMediaPlayer.setOnErrorListener(mErrorListener);
        //事件发生回调
        mMediaPlayer.setOnInfoListener(mInfoListener);
        //设置禁止锁屏，保持常亮
        mMediaPlayer.setScreenOnWhilePlaying(true);

    }

    /**
     * 开启视频
     */
    private void openVideo() {
        if (mUri == null && this.getHolder() == null) {
            return;
        }
        if (mMediaPlayer == null) {
            initMediaPLayer();
        } else {
            //为了重用处于error状态的mediaplayer对象，调用reset()方法，使其恢复到空闲状态
            mMediaPlayer.reset();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        // 设置需要播放的视频
        try {
            mMediaPlayer.setDataSource(mContext, mUri);
            bindSurfaceHolder(mMediaPlayer, getHolder());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 视频加载完成回调
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;
            //获得视频进度
            int duration = getDuration();
            if (mOnProgressListener != null) {
                //总进度回调
                mOnProgressListener.onTotleProgressListener(duration);
            }
            if (mOnProgressListener != null) {
                //加载完成回调
                mOnProgressListener.onPreparedListener(MyVideoView.this);
                //设置音量
                mMediaPlayer.setVolume(volumeNumber, volumeNumber);
            }
        }
    };

    /**
     * 播放完成回调
     */
    private MediaPlayer.OnCompletionListener mCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mCurrentState = STATE_PLAYBACK_COMPLETED;
                    if (mOnProgressListener != null) {
                        mOnProgressListener.onCompletion();
                    }

                    handler.removeCallbacks(progressRunnable);
                }
            };

    /**
     * 播放错误时回调
     */
    private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //隐藏当前view
            MyVideoView.this.setVisibility(GONE);
            mCurrentState = STATE_ERROR;
                    /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, what, extra)) {
                    return true;
                }
            }
            handler.removeCallbacks(progressRunnable);
            return true;
        }
    };

    /**
     * 事件发生回调
     */
    private MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (mOnInfoListener != null) {
                mOnInfoListener.onInfo(mp, what, extra);
            }
            switch (what) {
                case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                    break;
                case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                    break;
                case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                    break;
                case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                    break;
                case MediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                    Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                    break;
                case MediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                    Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                    break;
                default:
                    Log.d(TAG, "what:" + what + " extra:" + extra);
                    break;
            }
            return true;
        }
    };


    private void bindSurfaceHolder(MediaPlayer mediaPlayer, SurfaceHolder surfaceHolder) {
        if (mediaPlayer == null) {
            return;
        }
        if (surfaceHolder == null) {
            mediaPlayer.setDisplay(null);
            return;
        }
        mediaPlayer.setDisplay(surfaceHolder);
    }

    /**
     * Sets video URI.
     *
     * @param uri the URI of the video.
     */
    public void setVideoURI(Uri uri) {
        this.mUri = uri;
        openVideo();
    }


    /**
     * Sets vudei URL
     */
    public void setVideoURL(String url) {
        Uri uriFromUrl = Uri.parse(url);
        this.mUri = uriFromUrl;
        openVideo();
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != STATE_ERROR &&
                mCurrentState != STATE_IDLE &&
                mCurrentState != STATE_PREPARING);
    }

    @Override
    public void start() {
        if (isInPlaybackState()) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            handler.post(progressRunnable);
        }
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            handler.removeCallbacks(progressRunnable);
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getDuration();
        }
        return -1;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(pos);
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
        }
    }

    @Override
    public float getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }

    @Override
    public float getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }

    public OnProgressListener getmOnProgressListener() {
        return mOnProgressListener;
    }

    public void setmOnProgressListener(OnProgressListener mOnProgressListener) {
        this.mOnProgressListener = mOnProgressListener;
    }

    public OnErrorListener getmOnErrorListener() {
        return mOnErrorListener;
    }

    public void setmOnErrorListener(OnErrorListener mOnErrorListener) {
        this.mOnErrorListener = mOnErrorListener;
    }

    public OnInfoListener getmOnInfoListener() {
        return mOnInfoListener;
    }

    public void setmOnInfoListener(OnInfoListener mOnInfoListener) {
        this.mOnInfoListener = mOnInfoListener;
    }



    /**
     * 用来刷新进度条的handler
     */
    private Handler handler = new Handler();
    Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mOnProgressListener != null) {
                mOnProgressListener.onPlayProgress(getCurrentPosition());
            }
            //每次延迟1000毫秒再启动线程
            handler.postDelayed(progressRunnable, 1000);
        }
    };

}
