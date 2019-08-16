package com.founq.sdk.spannablestringlibrary;

import java.util.ArrayList;

/**
 * Created by ring on 2019/8/15.
 */
public interface AdapterOnClickListener {

    void onURLClickListener(String URL);

    void onImgClickListener(ArrayList<String> imgURLs);

    void onLongClickListener(String dealedContent);

}
