package com.cloudwebrtc.webrtc;

import android.util.Log;

import org.webrtc.CameraVideoCapturer;

// 사용자 정의 예외 클래스
class CameraTimeoutException extends Exception {
    public CameraTimeoutException(String message) {
        super(message);
    }

    public CameraTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

class CameraEventsHandler implements CameraVideoCapturer.CameraEventsHandler {
    public enum CameraState {
        NEW,
        OPENING,
        OPENED,
        CLOSED,
        DISCONNECTED,
        ERROR,
        FREEZED
    }
    private final static String TAG = FlutterWebRTCPlugin.TAG;
    private CameraState state = CameraState.NEW;

    public void waitForCameraOpen() {
        Log.d(TAG, "CameraEventsHandler.waitForCameraOpen");
        while (state != CameraState.OPENED && state != CameraState.ERROR) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForCameraClosed() {
        Log.d(TAG, "CameraEventsHandler.waitForCameraClosed");
        while (state != CameraState.CLOSED && state != CameraState.ERROR) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void waitForCameraOpenLimit() throws CameraTimeoutException {
        Log.d(TAG, "CameraEventsHandler.waitForCameraOpenLimit");
        long startTime = System.currentTimeMillis();

        while (state != CameraState.OPENED && state != CameraState.ERROR) {
            if (System.currentTimeMillis() - startTime > 1000) {
                state = CameraState.ERROR;
                Log.e(TAG, "(waitForCameraOpenLimit): Camera open timed out");
                throw new CameraTimeoutException("Camera open timed out after 1 second");
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while waiting for camera", e);
                state = CameraState.ERROR;
                throw new CameraTimeoutException("Interrupted during camera open wait", e);
            }
        }
    }

    public void waitForCameraClosedLimit() throws CameraTimeoutException {
        Log.d(TAG, "CameraEventsHandler.waitForCameraClosedLimit");
        long startTime = System.currentTimeMillis();
        while (state != CameraState.CLOSED && state != CameraState.ERROR) {
            if (System.currentTimeMillis() - startTime > 1000) {
                state = CameraState.ERROR;
                Log.e(TAG, "(waitForCameraClosedLimit): Camera close timed out");
                throw new CameraTimeoutException("Camera close timed out after 1 second");
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted while closing for camera", e);
                state = CameraState.ERROR;
                throw new CameraTimeoutException("Interrupted during camera closed wait", e);
            }
        }
    }

    // Camera error handler - invoked when camera can not be opened
    // or any camera exception happens on camera thread.
    @Override
    public void onCameraError(String errorDescription) {
        Log.d(TAG, String.format("CameraEventsHandler.onCameraError: errorDescription=%s", errorDescription));
        state = CameraState.ERROR;
    }

    // Called when camera is disconnected.
    @Override
    public void onCameraDisconnected() {
        Log.d(TAG, "CameraEventsHandler.onCameraDisconnected");
        state = CameraState.DISCONNECTED;
    }

    // Invoked when camera stops receiving frames
    @Override
    public void onCameraFreezed(String errorDescription) {
        Log.d(TAG, String.format("CameraEventsHandler.onCameraFreezed: errorDescription=%s", errorDescription));
        state = CameraState.FREEZED;
    }

    // Callback invoked when camera is opening.
    @Override
    public void onCameraOpening(String cameraName) {
        Log.d(TAG, String.format("CameraEventsHandler.onCameraOpening: cameraName=%s", cameraName));
        state = CameraState.OPENING;
    }

    // Callback invoked when first camera frame is available after camera is opened.
    @Override
    public void onFirstFrameAvailable() {
        Log.d(TAG, "CameraEventsHandler.onFirstFrameAvailable");
        state = CameraState.OPENED;
    }

    // Callback invoked when camera closed.
    @Override
    public void onCameraClosed() {
        Log.d(TAG, "CameraEventsHandler.onFirstFrameAvailable");
        state = CameraState.CLOSED;
    }
}