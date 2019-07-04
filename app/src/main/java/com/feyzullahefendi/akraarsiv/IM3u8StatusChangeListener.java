package com.feyzullahefendi.akraarsiv;

interface IM3u8StatusChangeListener {
    void onFinished();
    void onError(Exception exc);
    void onChunkFileDownloaded(int index, int size);
}
