package com.founq.sdk.spannablestringlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.founq.sdk.spannablestringlibrary.Delegate;
import com.founq.sdk.spannablestringlibrary.R;

import org.xml.sax.Attributes;

import java.util.Locale;

public class HtmlTextView extends AppCompatTextView {

    private Context mContext;

    private boolean isLongClick = false;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isLongClick){
                if (Delegate.sAdapterOnClickListener != null){
                    Delegate.sAdapterOnClickListener.onLongClickListener(Delegate.dealedString);
                }
            }
        }
    };

    public HtmlTextView(Context context) {
        super(context);
        mContext = context;
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public HtmlTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setShowText(String text, BufferType type) {
        CharSequence result;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            result = Html.fromHtml(text, 1, new MyImageGetter(mContext), new MyTagHandler());
//        } else {
//            result = Html.fromHtml(text, new MyImageGetter(mContext), new MyTagHandler());
//        }

        result = HtmlTagHandler.fromHtml(text,new MySmileGetter(mContext), new MyTagHandler());
        super.setText(result, type);
    }


    private long mLastActionDownTime = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CharSequence text = getText();
        if (text != null && text instanceof Spannable) {
            handleLinkMovementMethod(this, (Spannable) text, event);
        }

        return true;
//        return super.onTouchEvent(event);
    }

    private boolean handleLinkMovementMethod(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (action == MotionEvent.ACTION_UP) {
                long actionUpTime = System.currentTimeMillis();
                if (actionUpTime - mLastActionDownTime > ViewConfiguration.getLongPressTimeout()) { //长按事件，取消LinkMovementMethod处理，即不处理ClickableSpan点击事件
                    return false;
                }else {
                    isLongClick = false;
                }
                if (link.length != 0) {
                    link[0].onClick(widget);
                }
                Selection.removeSelection(buffer);
            } else if (action == MotionEvent.ACTION_DOWN) {
                if (link.length != 0) {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
                }
                mLastActionDownTime = System.currentTimeMillis();
                Message message = new Message();
                message.obj = buffer.toString();
                mHandler.sendMessageDelayed(message, ViewConfiguration.getLongPressTimeout());
                isLongClick = true;
            }

        }else {
            isLongClick = false;
        }
        return false;
    }

    public class MySmileGetter implements Html.ImageGetter {

        private Context mContext;
        private myDrawable drawable;

        public MySmileGetter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public myDrawable getDrawable(final String source) {
            String s = source.split("@")[0];
            drawable = new myDrawable();
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.icon_placeholder_loading)
                    .error(R.drawable.icon_placeholder_failed);
            RequestBuilder load = Glide.with(mContext)
                    .asDrawable()
                    .load(R.drawable.ic_123)
                    .apply(options);
            Target target = new DrawableTaget(drawable);
            load.into(target);
            return drawable;
        }

        class myDrawable extends BitmapDrawable {

            public void setDrawable(Drawable drawable) {
                this.drawable = drawable;
            }

            public Drawable getDrawable() {
                return drawable;
            }

            private Drawable drawable;

            @Override
            public void draw(@NonNull Canvas canvas) {
                if (drawable != null) {
                    drawable.draw(canvas);
                }
            }
        }

        class DrawableTaget implements Target<Drawable> {

            private myDrawable mDrawable;

            public DrawableTaget(myDrawable drawable) {
                mDrawable = drawable;
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                if (placeholder == null) {
                    return;
                }
                Rect rect = new Rect(0, 0, 40, 40);
                placeholder.setBounds(rect);
                mDrawable.setBounds(rect);
                mDrawable.setDrawable(placeholder);
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (errorDrawable == null) {
                    return;
                }
                Rect rect = new Rect(0, 0, 40, 40);
                errorDrawable.setBounds(rect);
                mDrawable.setBounds(rect);
                mDrawable.setDrawable(errorDrawable);
            }

            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                Rect rect = new Rect(0, 0, 40, 40);
                resource.setBounds(rect);
                mDrawable.setBounds(rect);
                mDrawable.setDrawable(resource);
                HtmlTextView.this.setText(HtmlTextView.this.getText());
                HtmlTextView.this.invalidate();
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }

            @Override
            public void getSize(@NonNull SizeReadyCallback cb) {
                cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
            }

            @Override
            public void removeCallback(@NonNull SizeReadyCallback cb) {

            }

            @Override
            public void setRequest(@Nullable Request request) {
            }

            @Nullable
            @Override
            public Request getRequest() {
                return null;
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onStop() {

            }

            @Override
            public void onDestroy() {

            }
        }
    }

    public class MyTagHandler implements HtmlTagHandler.TagHandler {

        @Override
        public void handleTag(boolean opening, String tag, Editable output, Attributes attrs) {


            //处理超链接标签
            if (tag.toLowerCase(Locale.getDefault()).equals("html")) {
                // 获取长度
                int len = output.length();
                URLSpan[] urls = output.getSpans(0, len, URLSpan.class);
                if (urls.length != 0) {
                    for (URLSpan urlSpan : urls) {
                        int start = output.getSpanStart(urlSpan);
                        int end = output.getSpanEnd(urlSpan);
                        String url = urlSpan.getURL();
                        output.removeSpan(urlSpan);
                        output.setSpan(new ClickableURL(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }
    }

    /**
     * 链接点击事件
     */
    private class ClickableURL extends ClickableSpan {

        private String url;

        public ClickableURL(String url) {
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            if (Delegate.sAdapterOnClickListener != null) {
                Delegate.sAdapterOnClickListener.onURLClickListener(url);
            }
//            Intent intent = new Intent();
//            intent.setAction("android.intent.action.VIEW");
//            intent.setData(Uri.parse(url));
//            mContext.startActivity(intent);
//            Intent intent = new Intent(mContext, WebActivity.class);
//            intent.putExtra("imgUri", url);
//            mContext.startActivity(intent);
        }
    }

}
