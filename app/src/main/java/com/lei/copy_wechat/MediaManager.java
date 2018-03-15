package com.lei.copy_wechat;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * Created by yanle on 2018/3/15.
 */

public class MediaManager {
    private static MediaPlayer mMediaPlay;
    //是否时暂停
    //private static boolean isStop;

    public static void playSound(String filePath, MediaPlayer.OnCompletionListener onCompletionListener) {
        if (mMediaPlay == null) {
            mMediaPlay = new MediaPlayer();
            mMediaPlay.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mMediaPlay.reset();
                    return false;
                }
            });
        } else {
            mMediaPlay.reset();

        }
        try {
            mMediaPlay.setDataSource(filePath);
            mMediaPlay.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlay.setOnCompletionListener(onCompletionListener);
            mMediaPlay.prepare();
            mMediaPlay.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopPlay() {
        if (mMediaPlay != null) {
            mMediaPlay.stop();
            mMediaPlay.release();
            mMediaPlay = null;
        }
    }
}
