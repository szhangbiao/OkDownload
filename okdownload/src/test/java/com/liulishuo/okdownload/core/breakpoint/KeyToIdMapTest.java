package com.liulishuo.okdownload.core.breakpoint;

import android.net.Uri;
import android.util.SparseArray;

import com.liulishuo.okdownload.DownloadTask;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.HashMap;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE)
public class KeyToIdMapTest {

    private KeyToIdMap map;

    private HashMap<String, Integer> keyToIdMap;
    private SparseArray<String> idToKeyMap;

    private DownloadTask task;

    @Before
    public void setup() {
        keyToIdMap = new HashMap<>();
        idToKeyMap = new SparseArray<>();

        map = new KeyToIdMap(keyToIdMap, idToKeyMap);

        task = mock(DownloadTask.class);
        when(task.getUrl()).thenReturn("url");
        when(task.getUri()).thenReturn(Uri.fromFile(new File("./file")));
        when(task.getFilename()).thenReturn(null);
    }

    @Test
    public void get() {
        assertThat(map.get(task)).isNull();

        keyToIdMap.put(map.generateKey(task), 1);
        assertThat(map.get(task)).isEqualTo(1);
    }

    @Test
    public void remove() {
        idToKeyMap.put(1, map.generateKey(task));
        keyToIdMap.put(map.generateKey(task), 1);
        map.remove(1);

        assertThat(keyToIdMap).isEmpty();
        assertThat(idToKeyMap.size()).isZero();
    }

    @Test
    public void add() {
        final String key = map.generateKey(task);

        map.add(task, 1);

        assertThat(keyToIdMap).containsKeys(key);
        assertThat(keyToIdMap).containsValues(1);
        assertThat(idToKeyMap.size()).isOne();
        assertThat(idToKeyMap.get(1)).isEqualTo(key);
    }
}