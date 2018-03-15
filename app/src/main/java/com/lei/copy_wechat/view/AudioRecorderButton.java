package com.lei.copy_wechat.view;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.lei.copy_wechat.R;

/**
 * Created by yanle on 2018/3/15.
 */

public class AudioRecorderButton extends AppCompatButton implements AudioManager.AudioStateListener {

    private static final int DISTANCE_Y_CANCEL = 50;
    private static final int STATE_NORMAL = 1;//默认状态
    private static final int STATE_RECODING = 2;//录音状态
    private static final int STATE_WANT_TO_CANCEL = 3;//取消状态
    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;
    private float mTime;
    //是否出发longClick
    private boolean mReader;

    private DialogManager mDialogManager;

    private AudioManager mAudiomanager;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        String dir = Environment.getExternalStorageDirectory() + "/WeChat_audios";
        mAudiomanager = AudioManager.getmInstance(dir);
        mAudiomanager.setOnAudioStateListener(this);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReader = true;
                //真正显示是在audio end prepare以后
                mAudiomanager.prepareAudio();
                //Log.i("info", "onLongClick");
                return false;
            }
        });
    }

    /**
     * 录音完成后的回调
     */
    public interface audioFinishRecorderListener {
        void onFinish(float seconds, String filePath);
    }

    private audioFinishRecorderListener mListener;

    public void setAudioFinishRecorderListener(audioFinishRecorderListener listener) {
        this.mListener = listener;
    }

    /**
     * 获取音量大小
     */
    private Runnable mGteVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {//已经在录制
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;//计时
                    mHandle.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private static final int MSG_AUDIO_PREPARED = 1000;//准备完毕
    private static final int MSG_VOICE_CHANGED = 2000;
    private static final int MSG_DIALOG_DIMISS = 3000;
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    //Log.i("info", "MSG_AUDIO_PREPARED");
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGteVoiceLevelRunnable).start();//获取音量
                    break;
                case MSG_VOICE_CHANGED:
                   // Log.i("info", "MSG_VOICE_CHANGED");
                    if (mCurState == STATE_RECODING)
                        mDialogManager.updateVoiceLevel(mAudiomanager.getVoiceLevel(6));
                    break;
                case MSG_DIALOG_DIMISS:
                    //Log.i("info", "MSG_DIALOG_DIMISS");
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override//准备好的回调
    public void wellPrepared() {
        mHandle.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECODING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {//已经开始录音
                    //根据x ,y 的坐标判断是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECODING);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                if (!mReader) {
                    reset();
                    return super.onTouchEvent(event);
                }
                if (!isRecording || mTime < 0.7f) {
                    mDialogManager.toShort();
                    mAudiomanager.cancel();
                    mHandle.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
                } else if (mCurState == STATE_RECODING) {//正常录制结束
                    //release
                    mDialogManager.dimissDialog();
                    mAudiomanager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudiomanager.getCurrentFilePath());
                    }
                    //callbackToAct
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    //cancel
                    mDialogManager.dimissDialog();
                    mAudiomanager.cancel();
                }
                reset();
                break;
        }


        return super.onTouchEvent(event);
    }

    //恢复状态及标志位
    private void reset() {
        isRecording = false;
        mReader = false;
        mTime = 0;
        changeState(STATE_NORMAL);
    }

    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }

        return false;
    }

    private void changeState(int mCurState) {
        if (this.mCurState != mCurState) {
            this.mCurState = mCurState;
            switch (mCurState) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recoder_normal);
                    break;
                case STATE_RECODING:
                    setBackgroundResource(R.drawable.btn_recorder);
                    setText(R.string.str_recoder_recoding);
                    if (isRecording) {
                        //Log.i("info", "STATE_RECODING");
                        mDialogManager.recording();
                        //Dialog.recording()
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    //Log.i("info", "STATE_WANT_TO_CANCEL");
                    setBackgroundResource(R.drawable.btn_recorder);
                    setText(R.string.str_recorder_want_canel);
                    //Dialog.want
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }


}
