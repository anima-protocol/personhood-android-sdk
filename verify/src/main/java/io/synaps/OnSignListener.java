package io.synaps;

public interface OnSignListener {
     void onSign(String payload, SignCallback callback, ErrorCallback errorCallback);
}
