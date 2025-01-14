package com.liulishuo.okdownload.core.connection;

import com.liulishuo.okdownload.RedirectUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DownloadUrlConnectionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private Proxy proxy;
    @Mock
    private URLConnection urlConnection;
    @Mock
    private URL url;
    @Mock
    private Map<String, List<String>> headerFields;
    @Mock
    private InputStream inputStream;
    private DownloadUrlConnection.RedirectHandler redirectHandler;
    private DownloadUrlConnection downloadUrlConnection;

    @Before
    public void setup() throws Exception {
        initMocks(this);

        Mockito.when(url.openConnection()).thenReturn(urlConnection);
        Mockito.when(url.openConnection(proxy)).thenReturn(urlConnection);

        redirectHandler = spy(new DownloadUrlConnection.RedirectHandler());
        downloadUrlConnection = spy(new DownloadUrlConnection(urlConnection, redirectHandler));
    }

    @Test
    public void construct_noConfiguration_noAssigned() throws IOException {
        DownloadUrlConnection.Factory factory = new DownloadUrlConnection.Factory();

        factory.create("https://jacksgong.com");

        verify(urlConnection, never()).setConnectTimeout(anyInt());
        verify(urlConnection, never()).setReadTimeout(anyInt());
    }

    @Test
    public void construct_validConfiguration_Assigned() throws IOException {
        DownloadUrlConnection.Factory factory = new DownloadUrlConnection.Factory(
                new DownloadUrlConnection.Configuration()
                        .proxy(proxy)
                        .connectTimeout(123)
                        .readTimeout(456)
        );

        factory.create(url);

        verify(url).openConnection(proxy);
        verify(urlConnection).setConnectTimeout(123);
        verify(urlConnection).setReadTimeout(456);
    }

    @Test
    public void addHeader() throws Exception {
        downloadUrlConnection.addHeader("name1", "value1");
        verify(urlConnection).addRequestProperty(eq("name1"), eq("value1"));
    }

    @Test
    public void execute() throws Exception {
        doNothing().when(redirectHandler).handleRedirect(any(DownloadConnection.class),
                any(DownloadConnection.Connected.class),
                ArgumentMatchers.<String, List<String>>anyMap());
        downloadUrlConnection.execute();
        verify(urlConnection).connect();
    }

    @Test
    public void handleRedirect() throws IOException {
        final DownloadUrlConnection.RedirectHandler handler = new DownloadUrlConnection.RedirectHandler();
        final Map<String, List<String>> headers = new HashMap<>();
        final String redirectLocation = "http://13.png";
        when(downloadUrlConnection.getResponseCode()).thenReturn(302).thenReturn(206);
        when(downloadUrlConnection.getResponseHeaderField("Location")).thenReturn(redirectLocation);
        doNothing().when(downloadUrlConnection).configUrlConnection();
        doNothing().when(urlConnection).connect();

        handler.handleRedirect(downloadUrlConnection, downloadUrlConnection, headers);

        verify(downloadUrlConnection).release();
        verify(downloadUrlConnection).configUrlConnection();
        verify(urlConnection).connect();
        assertThat(handler.getRedirectLocation()).isEqualTo(redirectLocation);
    }

    @Test
    public void handleRedirect_error() throws IOException {
        final DownloadUrlConnection.RedirectHandler handler =
                new DownloadUrlConnection.RedirectHandler();
        final Map<String, List<String>> headers = new HashMap<>();
        final String redirectLocation = "http://13.png";
        when(downloadUrlConnection.getResponseCode()).thenReturn(302);
        when(downloadUrlConnection.getResponseHeaderField("Location")).thenReturn(redirectLocation);
        doNothing().when(downloadUrlConnection).configUrlConnection();
        doNothing().when(urlConnection).connect();

        thrown.expect(ProtocolException.class);
        thrown.expectMessage("Too many redirect requests: "
                + (RedirectUtil.MAX_REDIRECT_TIMES + 1));
        handler.handleRedirect(downloadUrlConnection, downloadUrlConnection, headers);
    }

    @Test
    public void getResponseCode() throws Exception {
        assertThat(downloadUrlConnection.getResponseCode())
                .isEqualTo(DownloadConnection.NO_RESPONSE_CODE);
    }

    @Test
    public void getInputStream() throws Exception {
        when(urlConnection.getInputStream()).thenReturn(inputStream);
        assertThat(downloadUrlConnection.getInputStream()).isEqualTo(inputStream);
    }

    @Test
    public void getResponseHeaderFields() throws Exception {
        when(urlConnection.getHeaderFields()).thenReturn(headerFields);
        assertThat(downloadUrlConnection.getResponseHeaderFields()).isEqualTo(headerFields);
    }

    @Test
    public void getResponseHeaderField() throws Exception {
        when(urlConnection.getHeaderField("key1")).thenReturn("value1");
        assertThat(downloadUrlConnection.getResponseHeaderField("key1")).isEqualTo("value1");
    }

    @Test
    public void release() throws Exception {
        when(urlConnection.getInputStream()).thenReturn(inputStream);
        downloadUrlConnection.release();
        verify(inputStream).close();
    }

    @Test
    public void getRequestProperties() throws Exception {
        when(urlConnection.getRequestProperties()).thenReturn(headerFields);
        assertThat(downloadUrlConnection.getRequestProperties()).isEqualTo(headerFields);
    }

    @Test
    public void getRequestProperty() throws Exception {
        when(urlConnection.getRequestProperty("key1")).thenReturn("value1");
        assertThat(downloadUrlConnection.getRequestProperty("key1")).isEqualTo("value1");
    }

    @Test
    public void setRequestMethod() throws ProtocolException {
        final HttpURLConnection httpURLConnection = mock(HttpURLConnection.class);
        final DownloadUrlConnection connection = new DownloadUrlConnection(httpURLConnection);
        assertThat(connection.setRequestMethod("HEAD")).isTrue();
        verify(httpURLConnection).setRequestMethod(eq("HEAD"));

        assertThat(downloadUrlConnection.setRequestMethod("GET")).isFalse();
    }

    @Test
    public void getRedirectLocation() {
        final String redirectLocation = "http://13.png";
        redirectHandler.redirectLocation = redirectLocation;
        assertThat(downloadUrlConnection.getRedirectLocation()).isEqualTo(redirectLocation);
    }
}