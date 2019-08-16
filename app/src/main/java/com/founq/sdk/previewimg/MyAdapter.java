package com.founq.sdk.previewimg;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.founq.sdk.spannablestringlibrary.SpannableAdapter;

/**
 * Created by ring on 2019/8/16.
 */
public class MyAdapter extends SpannableAdapter {
    public MyAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder MyViewHolder(ViewGroup viewGroup, int type) {
        return null;
    }

    @Override
    public void bindMyViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

    }
}
