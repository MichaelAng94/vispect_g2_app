package com.vispect.android.vispect_g2_app.ui.widget;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.SurfaceViewWidthChangeCallback;
import com.vispect.android.vispect_g2_app.ui.activity.VMainActivity;
import com.vispect.android.vispect_g2_app.utils.XuLog;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

import interf.OnGetPicLisetener;


/**
 * 实时路况显示视频的surfaceview
 *
 * Created by xu on 2016/8/31.
 */
public class XuHCSurfaceView extends SurfaceView implements SurfaceHolder.Callback{

    private final static String TAG = "XuHSurfaceView";
    private SurfaceViewWidthChangeCallback widthChangeCallback;

    byte[] sps = { 0, 0, 0, 1, 103, 66, 0, 41, -115, -115, 64, 40, 2, -35, 0, -16, -120, 69, 56 };
    byte[] pps = { 0, 0, 0, 1, 104, -54, 67, -56 };

    SurfaceHolder holder;
    MediaCodec mCodec;
    int mCount = 0;
    // Video Constants
    private final static String MIME_TYPE = "video/avc"; // H.264 Advanced Video
    private final static int VIDEO_WIDTH = 1280;
    private final static int VIDEO_HEIGHT = 720;
    private final static int TIME_INTERNAL = 30;
    private volatile Thread runner = null;
    private boolean vieweCreated = false;

    private static OnGetPicLisetener listener;
    FramHandler  handler;
    HandlerThread h;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public XuHCSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {

            initDecoder(holder);
            XuLog.e(TAG, "调用了一次创建");
            h = new HandlerThread("fd");
            h.start();
            handler = new FramHandler(h.getLooper(),XuHCSurfaceView.this);
            PlayVideo();
            vieweCreated = true;
        }catch (Exception e){
            XuLog.e(TAG, "UDP创建的时候抛出了异常：" + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        XuLog.d("DrawAdas", "我调用了surfaceChanged");
        //显示区域发生变化 通知画框那边
        if(widthChangeCallback != null){
            widthChangeCallback.onWidthChange();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        XuLog.e(TAG,"我设置了不给画框");
        VMainActivity.drawable = false;
        vieweCreated = false;
        AppContext.getInstance().getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                stopVideo();
            }
        });

    }

    public void initDecoder(SurfaceHolder holder) {

        try {
            MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT);

            format.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
            format.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
            /* create & config android.media.MediaCodec */
            mCodec = MediaCodec.createDecoderByType(MIME_TYPE);
            mCodec.configure(format, holder.getSurface(), null, 0);//blind surfaceView

            mCodec.start(); //start decode thread

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public  boolean onFrame(byte[] buf, int offset, int length) {
//        Log.e(TAG, "onFrame start");
//        Log.e(TAG, "onFrame Thread:" + Thread.currentThread().getId());
        // Get input buffer index
        ByteBuffer[] inputBuffers = mCodec.getInputBuffers();
        int inputBufferIndex = mCodec.dequeueInputBuffer(100);
//
//        Log.e(TAG, "onFrame index:" + inputBufferIndex);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            inputBuffer.clear();
            inputBuffer.put(buf, offset, length);
            mCodec.queueInputBuffer(inputBufferIndex, 0, length, mCount
                    * TIME_INTERNAL, 0);
            mCount++;
        } else {
            return false;
        }

        // Get output buffer index
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 1000);
//        Log.e(TAG, "outputBufferIndex:"+outputBufferIndex);
        if(outputBufferIndex > 0){
            if(!VMainActivity.drawable && vieweCreated){
                VMainActivity.drawable = true;
                XuLog.e(TAG,"我设置了画框");
            }
        }
        while (outputBufferIndex >= 0) {
            mCodec.releaseOutputBuffer(outputBufferIndex, true);
            outputBufferIndex = mCodec.dequeueOutputBuffer(bufferInfo, 0);
        }

//        Log.e(TAG, "onFrame end");
        return true;
    }


    public void stopVideo() {
        XuLog.e(TAG,"UDP关闭");
        try {
            VMainActivity.drawable = false;
            AppContext.getInstance().getDeviceHelper().stopRealView();

        }catch (Exception e){
            XuLog.e(TAG,"UDP关闭的时候抛出了异常："+e.getMessage());
        }

    }
    public void setListener(OnGetPicLisetener listener) {
        this.listener = listener;
    }

    public void PlayVideo() {
        XuLog.e(TAG,"UDP打开");
        AppContext.getInstance().getDeviceHelper().startRealView();

    }

    private static class FramHandler extends Handler {
        SoftReference<XuHCSurfaceView> sf;
        XuHCSurfaceView t;

        public FramHandler(Looper l, XuHCSurfaceView t) {
            // TODO Auto-generated constructor stub
            super(l);
            sf = new SoftReference<XuHCSurfaceView>(t);
            this.t = t;
        }


        private boolean init = false;

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            try{
                byte[] buffer = (byte[]) msg.obj;

                if (init) {
                    t.onFrame(buffer, 0, buffer.length);
                } else {
                    // 0,0,0,1,67
                    if (buffer[0] == 0x00 && buffer[1] == 0x00 && buffer[2] == 0x00
                            && buffer[3] == 0x01 && buffer[4] == 0x67) {

                        t.onFrame(buffer, 0, buffer.length);
                        init = true;
                        XuLog.d("进行初始化");
                        if(listener!=null){
                            listener.onGetPic();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public void setOnePixData(byte[] buf, int size){
        try{
            final byte[] DataBuf = new byte[size];
            System.arraycopy(buf, 0, DataBuf, 0, size);

//            //将拿到的H264数据和长度传为handlerThread去硬解
            if (handler != null) {
                handler.obtainMessage(0, DataBuf).sendToTarget();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            XuLog.e("VMainActivity","XUCH返回键被按下了！");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setWidthChangeCallback(SurfaceViewWidthChangeCallback widthChangeCallback) {
        this.widthChangeCallback = widthChangeCallback;
    }
}
