package com.liulishuo.okdownload.core.file;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.channels.FileChannel;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.robolectric.annotation.Config.NONE;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = NONE, sdk = LOLLIPOP)
public class DownloadUriOutputStreamTest {

    @Mock private FileChannel channel;
    @Mock private ParcelFileDescriptor pdf;
    @Mock private BufferedOutputStream out;
    @Mock private FileOutputStream fos;
    @Mock private FileDescriptor fd;

    private DownloadUriOutputStream outputStream;

    @Before
    public void setup() {
        initMocks(this);

        outputStream = new DownloadUriOutputStream(channel, pdf, fos, out);
    }

    @Test(expected = FileNotFoundException.class)
    public void constructor_nullParcelFileDescriptor() throws FileNotFoundException {
        final Context context = mock(Context.class);
        final ContentResolver resolver = mock(ContentResolver.class);
        final Uri uri = mock(Uri.class);

        when(context.getContentResolver()).thenReturn(resolver);
        when(resolver.openFileDescriptor(uri, "rw")).thenReturn(null);

        new DownloadUriOutputStream(context, uri, 1);
    }

    @Test
    public void constructor() throws IOException {
        final Context context = mock(Context.class);
        final ContentResolver resolver = mock(ContentResolver.class);
        final ParcelFileDescriptor pdf = mock(ParcelFileDescriptor.class);
        final Uri uri = mock(Uri.class);
        final FileDescriptor fd = mock(FileDescriptor.class);

        when(context.getContentResolver()).thenReturn(resolver);
        when(resolver.openFileDescriptor(uri, "rw")).thenReturn(pdf);
        when(pdf.getFileDescriptor()).thenReturn(fd);

        final DownloadUriOutputStream outputStream = new DownloadUriOutputStream(context, uri, 1);
        assertThat(outputStream.pdf).isEqualTo(pdf);
        assertThat(outputStream.out).isNotNull();
        assertThat(outputStream.fos.getFD()).isEqualTo(fd);
    }

    @Test
    public void write() throws Exception {
        byte[] bytes = new byte[2];
        outputStream.write(bytes, 0, 1);
        verify(out).write(eq(bytes), eq(0), eq(1));
    }

    @Test
    public void close() throws Exception {
        outputStream.close();
        verify(out).close();
        verify(fos).close();
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // because of we invoke the native method, so this expected is means to invoked fd.sync
    @Test
    public void flushAndSync() throws Exception {
        when(pdf.getFileDescriptor()).thenReturn(fd);
        thrown.expect(SyncFailedException.class);
        thrown.expectMessage("sync failed");
        outputStream.flushAndSync();
        verify(out).flush();
    }

    @Test
    public void seek() throws Exception {
        outputStream.seek(1);
        verify(channel).position(eq(1L));
    }

    @Test
    public void setLength() {
        outputStream.setLength(1);
        verify(pdf).getFileDescriptor();
    }

    @Test
    public void factory() throws IOException {
        assertThat(new DownloadUriOutputStream.Factory().supportSeek()).isTrue();

        final Context context = mock(Context.class);
        final ContentResolver resolver = mock(ContentResolver.class);
        final ParcelFileDescriptor pdf = mock(ParcelFileDescriptor.class);
        final FileDescriptor fd = mock(FileDescriptor.class);
        final Uri uri = mock(Uri.class);

        when(context.getContentResolver()).thenReturn(resolver);
        when(resolver.openFileDescriptor(any(Uri.class), eq("rw"))).thenReturn(pdf);
        when(pdf.getFileDescriptor()).thenReturn(fd);

        final File file = new File("/test");
        DownloadUriOutputStream outputStream = (DownloadUriOutputStream) new DownloadUriOutputStream
                .Factory().create(context, file, 1);
        assertThat(outputStream.pdf).isEqualTo(pdf);
        assertThat(outputStream.out).isNotNull();
        assertThat(outputStream.fos.getFD()).isEqualTo(fd);

        outputStream = (DownloadUriOutputStream) new DownloadUriOutputStream.Factory()
                .create(context, uri, 1);
        assertThat(outputStream.pdf).isEqualTo(pdf);
        assertThat(outputStream.out).isNotNull();
        assertThat(outputStream.fos.getFD()).isEqualTo(fd);
    }
}