package com.yunqinglai.okdownload.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.yunqinglai.okdownload.R;

public abstract class BaseSampleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(titleRes()));
    }

    @StringRes
    protected abstract int titleRes();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_github) {
            openGithub();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openGithub() {
        Uri uri = Uri.parse(getString(R.string.github_url));
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
