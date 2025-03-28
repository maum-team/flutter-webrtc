package com.cloudwebrtc.webrtc;

import org.webrtc.MediaStreamTrack;

import android.util.Log;

public class LocalTrack {
    public LocalTrack(MediaStreamTrack track) {
        this.track = track;
    }

    public MediaStreamTrack track;
    private boolean isDisposed = false;

    public void dispose() {
        if (isDisposed) {
            return;
        }
        try {
            track.dispose();
        } catch (IllegalStateException e) {
            // 이미 dispose된 경우 예외가 발생할 수 있으므로 로그로 남기고 무시합니다.
            Log.w("LocalTrack", "Track already disposed: " + e.getMessage());
        }
        isDisposed = true;
    }

    public boolean enabled() {
        return track.enabled();
    }

    public void setEnabled(boolean enabled) {
        track.setEnabled(enabled);
    }

    public String id() {
        return track.id();
    }

    public String kind() {
        return track.kind();
    }
}
