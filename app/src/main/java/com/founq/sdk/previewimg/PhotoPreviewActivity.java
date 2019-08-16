package com.founq.sdk.previewimg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.founq.sdk.previewimg.widget.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class PhotoPreviewActivity extends AppCompatActivity {

    private List<String> imgURLs = new ArrayList<>();

    private PhotoView mPhotoView;

    private Target mTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_preview);
        mPhotoView = findViewById(R.id.my_img);
        imgURLs = getIntent().getStringArrayListExtra("imgURLs");
        if (imgURLs != null && imgURLs.size()>0){
            final RequestBuilder load;
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.icon_placeholder_loading)
                    .error(R.mipmap.ic_launcher);
            load = Glide.with(this).asBitmap()
                    .load(imgURLs.get(0))
                    .apply(options);
            mTarget = new MyTarget();
            load.into(mTarget);
        }
    }

    private class MyTarget implements Target<Bitmap> {

        @Override
        public void onLoadStarted(@Nullable Drawable placeholder) {

        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {

        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
            mPhotoView.setBitmap(resource);
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
