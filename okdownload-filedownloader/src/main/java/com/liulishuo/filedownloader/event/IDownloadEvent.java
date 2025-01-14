package com.liulishuo.filedownloader.event;

/**
 * An atom event.
 */
@SuppressWarnings({"WeakerAccess", "CanBeFinal"})
public abstract class IDownloadEvent {
    @SuppressWarnings("WeakerAccess")
    protected final String id;
    public Runnable callback = null;

    public IDownloadEvent(final String id) {
        this.id = id;
    }

    /**
     * @see #IDownloadEvent(String)
     * @deprecated do not handle ORDER any more.
     */
    public IDownloadEvent(final String id, boolean order) {
        this.id = id;
    }

    public final String getId() {
        return this.id;
    }
}
