package com.liulishuo.okdownload.core;

import com.liulishuo.okdownload.DownloadTask;

import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IdentifiedTaskTest {
    private IdentifiedTask task;
    private IdentifiedTask another;
    private String url = "https://jacksgong.com";
    private File providedFile = new File("/provided-path/filename");
    private File parentFile = new File("/provided-path");
    private String filename = "filename";

    @Before
    public void setUp() throws Exception {
        task = spy(new IdentifiedTask() {
            @Override
            public int getId() {
                return 0;
            }

            @NonNull
            @Override
            public String getUrl() {
                return url;
            }

            @NonNull
            @Override
            protected File getProvidedPathFile() {
                return providedFile;
            }

            @NonNull
            @Override
            public File getParentFile() {
                return parentFile;
            }

            @Nullable
            @Override
            public String getFilename() {
                return filename;
            }
        });

        another = spy(new IdentifiedTask() {
            @Override
            public int getId() {
                return 0;
            }

            @NonNull
            @Override
            public String getUrl() {
                return url;
            }

            @NonNull
            @Override
            protected File getProvidedPathFile() {
                return providedFile;
            }

            @NonNull
            @Override
            public File getParentFile() {
                return parentFile;
            }

            @Nullable
            @Override
            public String getFilename() {
                return filename;
            }
        });
    }

    @Test
    public void compareIgnoreId_url() {
        when(another.getUrl()).thenReturn("another-url");
        assertThat(task.compareIgnoreId(another)).isFalse();

        when(another.getUrl()).thenReturn(url);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_providedPathFile() {
        when(another.getProvidedPathFile()).thenReturn(new File("/another-provided-path"));
        when(another.getParentFile()).thenReturn(new File("/another-provided-path"));
        assertThat(task.compareIgnoreId(another)).isFalse();

        when(another.getProvidedPathFile()).thenReturn(providedFile);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_parentPath() {
        when(another.getProvidedPathFile()).thenReturn(new File("/another-parent-path"));

        when(another.getParentFile()).thenReturn(new File("/another-parent-path"));
        assertThat(task.compareIgnoreId(another)).isFalse();

        when(another.getParentFile()).thenReturn(parentFile);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_filename() {
        when(another.getProvidedPathFile()).thenReturn(new File("/another-parent-path"));

        when(another.getFilename()).thenReturn(null);
        assertThat(task.compareIgnoreId(another)).isFalse();

        when(another.getFilename()).thenReturn("another-filename");
        assertThat(task.compareIgnoreId(another)).isFalse();

        when(another.getFilename()).thenReturn(filename);
        assertThat(task.compareIgnoreId(another)).isTrue();
    }

    @Test
    public void compareIgnoreId_falseEmpty() {
        final IdentifiedTask task = DownloadTask.mockTaskForCompare(1);
        final IdentifiedTask anotherTask = DownloadTask.mockTaskForCompare(2);

        assertThat(task.compareIgnoreId(task)).isFalse();
        assertThat(task.compareIgnoreId(anotherTask)).isFalse();
    }
}