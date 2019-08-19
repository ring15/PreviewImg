package com.founq.sdk.spannablestringlibrary;

import java.util.ArrayList;

/**
 * Created by ring on 2019/8/15.
 */
public interface AdapterOnClickListener {

    void onURLClickListener(String URL);

    void onImgClickListener(String imgURL);

    void onImgsClickListener(ArrayList<String> imgURLs, int index);

    void onLongClickListener(String dealedContent);

}
