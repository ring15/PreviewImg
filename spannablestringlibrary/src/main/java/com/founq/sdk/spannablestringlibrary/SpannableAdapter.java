package com.founq.sdk.spannablestringlibrary;

import android.content.Context;
import android.text.method.ArrowKeyMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.founq.sdk.spannablestringlibrary.widget.HtmlTagHandler;
import com.founq.sdk.spannablestringlibrary.widget.HtmlTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ring on 2019/8/15.
 */
public abstract class SpannableAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int TYPE_TEXT = -1;
    private final static int TYPE_PHOTO = -2;

    private Context mContext;

    private String mSpannableString;

    private int beforeSize;
    private List<Integer> beforeType;

    private int afterSize;
    private List<Integer> afterType;

    //用来存放拆分后的内容
    private List<String> contents = new ArrayList<>();
    //用来存放，当前内容是否为图片链接
    private List<Boolean> isPhoto = new ArrayList<>();

    public SpannableAdapter(Context context) {
        mContext = context;
    }

    public void setSpannableString(String spannableString) {
        mSpannableString = spannableString;
        dealString();
    }

    public void setAdapterOnClickListener(AdapterOnClickListener adapterOnClickListener) {
        Delegate.sAdapterOnClickListener = adapterOnClickListener;
    }

    private void dealString() {
        String dealString = "";
        //通过<img>标签进行拆分
        String[] firstSplite = mSpannableString.split("<img>");
        for (int i = 0; i < firstSplite.length; i++) {
            //判断该部分中是否含有</img>标签， 没有，则直接添加到内容区域
            if (!firstSplite[i].matches(".*</img>.*")) {
                contents.add(firstSplite[i]);
                isPhoto.add(false);
                dealString += firstSplite[i];
            } else {
                String[] secondSplite = firstSplite[i].split("</img>");
                for (int j = 0; j < secondSplite.length; j++) {
                    contents.add(secondSplite[j]);
                    isPhoto.add(j == 0);
                    if (j != 0) {
                        dealString += secondSplite[j];
                    }
                }
            }
        }
        Delegate.dealedString = HtmlTagHandler.fromHtml(dealString).toString();
    }

    public void setBeforeSize(int beforeSize, List<Integer> beforeType) {
        this.beforeSize = beforeSize;
        this.beforeType = beforeType;
    }

    public void setAfterSize(int afterSize, List<Integer> afterType) {
        this.afterSize = afterSize;
        this.afterType = afterType;
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if (position < beforeSize) {
            type = beforeType.get(position);
        } else if (contents != null && position < beforeSize + contents.size()) {
            if (isPhoto.get(position - beforeSize)) {
                type = TYPE_PHOTO;
            } else {
                type = TYPE_TEXT;
            }
        } else {
            if (contents != null) {
                type = afterType.get(position - beforeSize - contents.size());
            } else {
                type = afterType.get(position - beforeSize);
            }
        }
        return type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        if (i == TYPE_TEXT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_text, viewGroup, false);
            holder = new TextViewHolder(view);
        } else if (i == TYPE_PHOTO) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_photo, viewGroup, false);
            holder = new PhotoViewHolder(view);
        } else {
            holder = MyViewHolder(viewGroup, i);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof TextViewHolder) {
            //通过HtmlTextView可以识别一些其他标签，比如字体加黑
            TextViewHolder holder = (TextViewHolder) viewHolder;
            holder.htmlTvTest.setShowText(contents.get(i - beforeSize), null);
            holder.htmlTvTest.setMovementMethod(ArrowKeyMovementMethod.getInstance());
            holder.htmlTvTest.setTextIsSelectable(true);
        } else if (viewHolder instanceof PhotoViewHolder) {
            //加载图片
            PhotoViewHolder holder = (PhotoViewHolder) viewHolder;
            Glide.with(mContext)
                    .load(contents.get(i - beforeSize))
                    .into(holder.mPhotoImg);

            holder.mPhotoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Delegate.sAdapterOnClickListener != null) {
                        Delegate.sAdapterOnClickListener.onImgClickListener(contents.get(i - beforeSize));
                    }
                }
            });
            holder.mPhotoImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Delegate.sAdapterOnClickListener != null) {
                        Delegate.sAdapterOnClickListener.onLongClickListener(Delegate.dealedString);
                    }
                    return true;
                }
            });
        } else {
            bindMyViewHolder(viewHolder, i);
        }
    }

    @Override
    public int getItemCount() {
        return contents == null ? beforeSize + afterSize : contents.size() + beforeSize + afterSize;
    }


    //文本加载layout
    public class TextViewHolder extends RecyclerView.ViewHolder {
        private HtmlTextView htmlTvTest;

        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            htmlTvTest = itemView.findViewById(R.id.html_test);
        }
    }

    //图片加载layout
    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        private ImageView mPhotoImg;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            mPhotoImg = itemView.findViewById(R.id.img_photo);
        }
    }


    public abstract RecyclerView.ViewHolder MyViewHolder(ViewGroup viewGroup, int type);

    public abstract void bindMyViewHolder(RecyclerView.ViewHolder viewHolder, int i);

}
