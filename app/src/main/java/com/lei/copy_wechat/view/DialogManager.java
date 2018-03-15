package com.lei.copy_wechat.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lei.copy_wechat.R;

/**
 * Created by yanle on 2018/3/15.
 */

public class DialogManager {
    private Dialog mDialog;

    private ImageView mVoice;
    private TextView mLable;

    private Context mContext;

    public DialogManager(Context context) {
        this.mContext = context;
    }

    //显示的第一个dialog，也是默认的一个样子
    public void showRecordingDialog() {
        mDialog = new Dialog(mContext, R.style.ThemeAudioDialog);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_recorder, null);
        mDialog.setContentView(view);

        mVoice = mDialog.findViewById(R.id.iv_dialog_voice);
        mLable = mDialog.findViewById(R.id.tv_dialog_label);
        //Log.i("tag", "showRecordingDialog");
        mDialog.show();
    }

    //录音时
    public void recording() {
        if (mDialog != null && mDialog.isShowing()) {
//            mVoice.setVisibility(View.VISIBLE);
//            mLable.setVisibility(View.VISIBLE);
           // Log.i("tag", "recording");
            //mVoice.setImageResource(R.drawable.ic_loading1);
            mLable.setText(R.string.str_dialog_want_canel);
        }
    }

    //取消发送的dialog
    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
//            mVoice.setVisibility(View.VISIBLE);
//            mLable.setVisibility(View.VISIBLE);
            //Log.i("tag", "wantToCancel");
            mVoice.setImageResource(R.drawable.stop);
            mLable.setText(R.string.str_recorder_want_canel);
        }
    }

    //显示时间短的对话框
    public void toShort() {
        if (mDialog != null && mDialog.isShowing()) {
//            mVoice.setVisibility(View.VISIBLE);
//            mLable.setVisibility(View.VISIBLE);
            //Log.i("tag", "toShort");
            mVoice.setImageResource(R.drawable.to_short);
            mLable.setText(R.string.to_short);
        }
    }

    //隐藏对话框
    public void dimissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
            //Log.i("tag", "dimissDialog");
        }
    }

    /**
     * 通过level去更新voice上的图片
     *
     * @param
     */
    //更新s对话框上的音量的标志
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {
//            mVoice.setVisibility(View.VISIBLE);
//            mLable.setVisibility(View.VISIBLE);
            int resId = mContext.getResources().getIdentifier("ic_loading" + level, "drawable", mContext.getPackageName());
            mVoice.setImageResource(resId);

        }
    }
}
