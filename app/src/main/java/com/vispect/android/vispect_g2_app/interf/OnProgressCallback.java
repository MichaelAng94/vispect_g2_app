package com.vispect.android.vispect_g2_app.interf;

/**
 * Created by xu on 2017/10/31.
 */
public interface OnProgressCallback {
    /**
     * The method that will be called when progress is made.
     *
     * @param current the current value of the progress of the request.
     * @param max     the maximum value (target) value that the progress will have.
     */
    void onProgress(float current, float max);
}
