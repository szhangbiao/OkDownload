package com.liulishuo.okdownload;

import androidx.annotation.Nullable;

import com.liulishuo.okdownload.core.connection.DownloadConnection;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IRedirectHandler {

    /**
     * handle redirect during connection
     *
     * @param originalConnection original connection of original url, contain connection info
     * @param originalConnected  connected connection of original url, contain request response
     *                           of first connect
     * @param headerProperties   request headers of the connection, these headers should be added in
     *                           the new connection during handle redirect
     */
    void handleRedirect(DownloadConnection originalConnection, DownloadConnection.Connected originalConnected, Map<String, List<String>> headerProperties) throws IOException;

    @Nullable
    String getRedirectLocation();
}
