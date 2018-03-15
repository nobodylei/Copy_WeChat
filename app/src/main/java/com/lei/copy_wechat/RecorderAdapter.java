package com.lei.copy_wechat;

import android.content.Context;
import android.nfc.tech.MifareClassic;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yanle on 2018/3/15.
 */

public class RecorderAdapter extends ArrayAdapter<MainActivity.Recorder> {
    private List<MainActivity.Recorder> mDatas;
    private Context mContext;
    private int mMinItemWidth;
    private int mMaxItemWidth;
    private LayoutInflater mInflater;

    public RecorderAdapter(@NonNull Context context, List<MainActivity.Recorder> datas) {
        super(context, -1, datas);
        mContext = context;
        mDatas = datas;
        mInflater = LayoutInflater.from(context);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //拿到最下值和最大值
        mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f);
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.15);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null) {
            convertView = mInflater.inflate(R.layout.item_recorder, parent,false);
            holder = new ViewHolder();
            holder.secounds = convertView.findViewById(R.id.tv_recorder_time);
            holder.lenght = convertView.findViewById(R.id.fl_recorder_length);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.secounds.setText(Math.round(getItem(position).time) + "\"");
        ViewGroup.LayoutParams lp = holder.lenght.getLayoutParams();
        lp.width = (int) (mMinItemWidth + mMaxItemWidth / 60f * getItem(position).time);

        return convertView;
    }

    private class ViewHolder {
        TextView secounds;
        View lenght;
    }
}
