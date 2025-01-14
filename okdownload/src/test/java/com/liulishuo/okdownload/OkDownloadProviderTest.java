package com.liulishuo.okdownload;

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class OkDownloadProviderTest {

    private OkDownloadProvider provider;
    @Mock Uri uri;

    @Before
    public void setup() {
        initMocks(this);
        provider = new OkDownloadProvider();
    }

    @Test
    public void onCreate() {
        assertThat(provider.onCreate()).isTrue();
    }

    @Test
    public void query() {
        assertThat(provider.query(uri, null, null, null, null)).isNull();
    }

    @Test
    public void getType() {
        assertThat(provider.getType(uri)).isNull();
    }

    @Test
    public void insert() {
        assertThat(provider.insert(uri, null)).isNull();
    }

    @Test
    public void delete() {
        assertThat(provider.delete(uri, null, null)).isZero();
    }

    @Test
    public void update() {
        assertThat(provider.update(uri, null, null, null)).isZero();
    }
}