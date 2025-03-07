package com.liulishuo.filedownloader.connection;

import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * The FileDownloadConnection implemented using {@link URLConnection}.
 */

public class FileDownloadUrlConnection implements FileDownloadConnection {
    protected URLConnection mConnection;

    public FileDownloadUrlConnection(String originUrl, Configuration configuration) throws IOException {
        this(new URL(originUrl), configuration);
    }

    public FileDownloadUrlConnection(URL url, Configuration configuration) throws IOException {
        if (configuration != null && configuration.mProxy != null) {
            mConnection = url.openConnection(configuration.mProxy);
        } else {
            mConnection = url.openConnection();
        }
        if (mConnection instanceof HttpURLConnection) {
            ((HttpURLConnection) mConnection).setInstanceFollowRedirects(false);
        }

        if (configuration != null) {
            if (configuration.mReadTimeout != null) {
                mConnection.setReadTimeout(configuration.mReadTimeout);
            }

            if (configuration.mConnectTimeout != null) {
                mConnection.setConnectTimeout(configuration.mConnectTimeout);
            }
        }
    }

    public FileDownloadUrlConnection(String originUrl) throws IOException {
        this(originUrl, null);
    }

    @Override
    public void addHeader(String name, String value) {
        mConnection.addRequestProperty(name, value);
    }

    @Override
    public boolean dispatchAddResumeOffset(String etag, long offset) {
        return false;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mConnection.getInputStream();
    }

    @Override
    public Map<String, List<String>> getRequestHeaderFields() {
        return mConnection.getRequestProperties();
    }

    @Override
    public Map<String, List<String>> getResponseHeaderFields() {
        return mConnection.getHeaderFields();
    }

    @Override
    public String getResponseHeaderField(String name) {
        return mConnection.getHeaderField(name);
    }

    @Override
    public boolean setRequestMethod(String method) throws ProtocolException {
        if (mConnection instanceof HttpURLConnection) {
            ((HttpURLConnection) mConnection).setRequestMethod(method);
            return true;
        }
        return false;
    }

    @Override
    public void execute() throws IOException {
        mConnection.connect();
    }

    @Override
    public int getResponseCode() throws IOException {
        if (mConnection instanceof HttpURLConnection) {
            return ((HttpURLConnection) mConnection).getResponseCode();
        }
        return FileDownloadConnection.NO_RESPONSE_CODE;
    }

    @Override
    public void ending() {
        try {
            mConnection.getInputStream().close();
        } catch (IOException ignored) {
        }
    }


    public static class Creator implements FileDownloadHelper.ConnectionCreator {
        private final Configuration mConfiguration;

        public Creator() {
            this(null);
        }

        public Creator(Configuration configuration) {
            this.mConfiguration = configuration;
        }

        FileDownloadConnection create(URL url) throws IOException {
            return new FileDownloadUrlConnection(url, mConfiguration);
        }

        @Override
        public FileDownloadConnection create(String originUrl) throws IOException {
            return new FileDownloadUrlConnection(originUrl, mConfiguration);
        }
    }

    /**
     * The sample configuration for the {@link FileDownloadUrlConnection}
     */
    public static class Configuration {
        private Proxy mProxy;
        private Integer mReadTimeout;
        private Integer mConnectTimeout;

        /**
         * The connection will be made through the specified proxy.
         * <p>
         * This {@code proxy} will be used when invoke {@link URL#openConnection(Proxy)}
         *
         * @param proxy the proxy will be applied to the {@link FileDownloadUrlConnection}
         */
        public Configuration proxy(Proxy proxy) {
            this.mProxy = proxy;
            return this;
        }

        /**
         * Sets the read timeout to a specified timeout, in milliseconds. A non-zero value specifies
         * the timeout when reading from Input stream when a connection is established to a resource
         * <p>
         * If the timeout expires before there is data available for read, a
         * java.net.SocketTimeoutException is raised. A timeout of zero is interpreted as an
         * infinite timeout.
         * <p>
         * This {@code readTimeout} will be applied through
         * {@link URLConnection#setReadTimeout(int)}
         *
         * @param readTimeout an <code>int</code> that specifies the timeout value to be used in
         *                    milliseconds
         */
        public Configuration readTimeout(int readTimeout) {
            this.mReadTimeout = readTimeout;
            return this;
        }

        /**
         * Sets a specified timeout value, in milliseconds, to be used when opening a communications
         * link to the resource referenced by this URLConnection.  If the timeout expires before the
         * connection can be established, a java.net.SocketTimeoutException is raised. A timeout of
         * zero is interpreted as an infinite timeout.
         * <p>
         * This {@code connectionTimeout} will be applied through
         * {@link URLConnection#setConnectTimeout(int)}
         *
         * @param connectTimeout an <code>int</code> that specifies the connect timeout value in
         *                       milliseconds
         */
        public Configuration connectTimeout(int connectTimeout) {
            this.mConnectTimeout = connectTimeout;
            return this;
        }
    }
}
