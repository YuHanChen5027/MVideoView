package com.example.mvideoview;

import android.media.MediaPlayer;

/**
 * Created by Chenyuhan on 2017/11/28.
 */

public interface IMediaPlayer {
    void start();

    void pause();

    int getDuration();

    int getCurrentPosition();

    float getVideoWidth();

    float getVideoHeight();

    void seekTo(int pos);

    boolean isPlaying();

    void release();

    /**
     * 进度监听接口
     */
    interface OnProgressListener {

        /**
         * surfaceview准备完成后调用
         */
        void onSurfaceCreated();

        /**
         * 视频信息加载完成后调用
         *
         * @param mediaPlayer
         */
        void onPreparedListener(IMediaPlayer mediaPlayer);

        /**
         * 视频总进度信息监听
         *
         * @param totalProgress 视频长度（单位:ms）
         */
        void onTotleProgressListener(int totalProgress);

        /**
         * 视频播放进度监听
         *
         * @param progress 当前播放进度（单位:ms）
         */
        void onPlayProgress(int progress);

        /**
         * 播放完成监听
         */
        void onCompletion();


    }

    /**
     * 错误监听接口
     */
    interface OnErrorListener {
        boolean onError(MediaPlayer var1, int var2, int var3);
    }

    /**
     * 事件发生接口
     */
    interface OnInfoListener {
        boolean onInfo(MediaPlayer var1, int var2, int var3);
    }

}
