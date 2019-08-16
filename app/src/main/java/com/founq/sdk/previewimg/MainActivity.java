package com.founq.sdk.previewimg;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.founq.sdk.spannablestringlibrary.AdapterOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterOnClickListener {

    private RecyclerView mRecyclerView;

    private MyAdapter mMyAdapter;

    private String s2 = "<p><a href=\"http://h5.shumensy.com/download/game\">蜀门手游下载</a></p><p><a href=\"http://h5.shumensy.com/download/forum\">" +
            "社区下载</a></p><br/><p>当国民级热血仙侠IP巨制《蜀门手游》与顶级热血国漫作者碰撞，会产出怎样的火花？10月20日，由方趣网络主办的《蜀门手游》&ldquo;" +
            "玩乐3次方&rdquo;品牌发布会将在上海黄浦江畔盛大举行。本次发布会现场不仅会公布游戏嘉年华版本后续更新动作，更有《镇魂街》漫画作者许辰亮相。" +
            "届时，由他亲述为《蜀门手游》创作的新职业墨韵创作的历程，以及揭晓新职业墨韵全平台上线的时间。现场更设有新职业试玩体验专区以及神秘互动环节，令人万分期待！</p>" +
            "忍者必须死三的周年庆就要到了！我们为大家准备了很多有趣的福利活动，同时也会有一个盛大的线下派对将会举行！首先感谢亲爱的忍忍们的一路相伴！" +
            "<br/><br/>家族一向是忍忍们在忍界的小家庭，那么，你想不想让自己的家族在周年庆派对华丽登场呢？在本帖的楼层中留下自己家族精美的海报吧！" +
            "<br/><strong>我们将会筛选出其中的数十份海报，并打印出来展示在派对中哦！<br/></strong>" +
            "<smile src=\"123\">" +
            "如果想让自己家族的海报出现在周年庆现场，除了精美的设计，还有要记得发送高分辨率的高清图片~<br/><br/>" +
            "&nbsp;假白在线举例：<br/><!-- IMG0 -->这浓浓的气死设计师的风格是个什么鬼啦！<br/><br/>本次派对，正是你心爱的家族上镜的大好机会！" +
            "快将家族大佬精心设计出的海报亮出来吧！千万不要学习假白哟~<strong>期待在周年庆派对，能看到你的家族海报亮相！</strong>" +
            "<smile src=\"456\">" +
            "测试1" +
            "<img>http://founq-shumen.oss-cn-shanghai.aliyuncs.com/184659/15644567627871846590.jpg</img>测试2" +
            "<img>https://img2.tapimg.com/bbcode/images/54a9b9c42e181cd2ee7bf73afb30fb62.jpg?imageView2/2/w/984/q/40/format/jpg/interlace/1/ignore-error/1</img>测试2" +
            "<img>http://founq-shumen.oss-cn-shanghai.aliyuncs.com/184659/15644567718511846590.jpg</img>" +
            "<img>https://img2.tapimg.com/bbcode/images/2a42eb3e4f0a6f317fdee5e7a4081aac.png?imageView2/2/w/984/q/40/format/jpg/interlace/1/ignore-error/1</img>" +
            "<img>https://img2.tapimg.com/bbcode/images/712ff4b8b38414db20b1fba82f1c1e46.png?imageView2/2/w/984/q/40/format/jpg/interlace/1/ignore-error/1</img>" +
            "<img>https://img2.tapimg.com/bbcode/images/0091d10fa150f6d1b18b11c74fa3d8ab.png?imageView2/2/w/984/q/40/format/jpg/interlace/1/ignore-error/1</img>" +
            "<img>https://img2.tapimg.com/bbcode/images/f20f6ce6b56a61b0e053f9c45caf2418.png?imageView2/2/w/984/q/40/format/jpg/interlace/1/ignore-error/1</img>" +
            "测试3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mMyAdapter = new MyAdapter(this);
        mMyAdapter.setSpannableString(s2);
        mMyAdapter.setAdapterOnClickListener(this);
        mRecyclerView.setAdapter(mMyAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onURLClickListener(String URL) {

    }

    @Override
    public void onImgClickListener(ArrayList<String> imgURLs) {
        Intent intent = new Intent(MainActivity.this, PhotoPreviewActivity.class);
        intent.putStringArrayListExtra("imgURLs", imgURLs);
        startActivity(intent);
    }

    @Override
    public void onLongClickListener(String dealedContent) {

    }
}
