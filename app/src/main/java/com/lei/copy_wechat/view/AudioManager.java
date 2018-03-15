package com.lei.copy_wechat.view;

import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

/**
 * Created by yanle on 2018/3/15.
 */

public class AudioManager {
    //录制媒体接口
    private MediaRecorder mMediaRecorder;
    private String mDir;//文件夹
    private String mCurrentFilePath;
    private boolean isPrepared;
    private static AudioManager mInstance;


    private AudioManager(String dir) {
        this.mDir = dir;
    }

    public String getCurrentFilePath() {
        return mCurrentFilePath;
    }

    /**
     * 回调准备完毕
     */
    public interface AudioStateListener {
        void wellPrepared();
    }

    public AudioStateListener mListener;

    public void setOnAudioStateListener(AudioStateListener mListener) {
        this.mListener = mListener;
    }

    public static AudioManager getmInstance(String dir) {
        if (mInstance == null) {
            synchronized (AudioManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioManager(dir);
                }
                ;
            }
        }
        return mInstance;
    }

    public void prepareAudio() {//准备
        try {
            isPrepared = false;
            File dir = new File(mDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = generateFileName();
            File file = new File(dir, fileName);
            mCurrentFilePath = file.getAbsolutePath();

            mMediaRecorder = new MediaRecorder();
            //设置输出文件
            mMediaRecorder.setOutputFile(file.getAbsolutePath());
            //设置音频源为麦克风
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            //设置音频格式
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            //设置音频编码为amr
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            isPrepared = true;//准备结束
            if(mListener != null) {//通知Button准备好了
                mListener.wellPrepared();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 随机生成文件名称
     *
     * @return
     */
    private String generateFileName() {
        return System.currentTimeMillis() + ".amr";
    }

    public int getVoiceLevel(int maxLeval) {//获取音量等级
        if(isPrepared) {
            try {
                //mMediaRecorder.getMaxAmplitude()范围1-32767
                return maxLeval * mMediaRecorder.getMaxAmplitude() / 32768 + 1;
            } catch (Exception e){

            }
        }
        return 1;
    }

    public void release() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
    }

    public void cancel() {//取消
        release();
        if(mCurrentFilePath != null) {
            File file = new File(mCurrentFilePath);
            file.delete();
            mCurrentFilePath = null;
        }
    }
}
