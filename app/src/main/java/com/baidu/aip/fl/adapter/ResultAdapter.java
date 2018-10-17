package com.baidu.aip.fl.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.baidu.aip.fl.bean.User;
import com.hearing.R;

import java.util.List;

/**
 * Create by hearing on 18-10-17
 */
public class ResultAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<User> mData;

    public ResultAdapter(Context context, List<User> data) {
        mContext = context;
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;//面向应用的人脸识别签到系统的研究和开发
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_result, null);
                holder.text = convertView.findViewById(R.id.item_tv);
            } else {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_result, null);
                holder.text = convertView.findViewById(R.id.item_tv);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.text.setText((position+1)+": "+mData.get(position).toString());
        return convertView;
    }

    public final class ViewHolder {
        public TextView text;
    }
}
