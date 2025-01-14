package com.yunqinglai.okdownload;


import android.os.Bundle;

import androidx.annotation.Nullable;

import com.liulishuo.okdownload.core.Util;
import com.yunqinglai.okdownload.base.BaseListActivity;

public class MainActivity extends BaseListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.enableConsoleLog();
    }

    @Override
    protected void setupAdapter(BaseListActivity.ItemsHolder holder) {
        holder.addItem(R.string.single_download_title, R.string.single_download_desc, SingleActivity.class);
        holder.addItem(R.string.each_block_progress_title, R.string.each_block_progress_desc, EachBlockProgressActivity.class);
        holder.addItem(R.string.queue_download_title, R.string.queue_download_desc, QueueActivity.class);
        holder.addItem(R.string.bunch_download_title, R.string.bunch_download_desc, BunchActivity.class);
        holder.addItem(R.string.title_content_uri, R.string.content_uri_desc, ContentUriActivity.class);
        holder.addItem(R.string.title_notification, R.string.notification_desc, NotificationActivity.class);
    }

    @Override
    public int titleRes() {
        return R.string.app_name;
    }
}
