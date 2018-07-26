package com.vispect.android.vispect_g2_app.bean;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

/**
 * 一条CAN数据
 *
 * Created by xu on 2017/1/13.
 */
public class OBDValue {

   private String id;
    private String value;
    private boolean changed = false;
    private ArrayList<Integer> start = new ArrayList<>();
    private ArrayList<Integer> blue = new ArrayList<>();
    private Handler changeHandler = new Handler(Looper.getMainLooper());
    Runnable resetChange = new Runnable() {
        @Override
        public void run() {
            changed = !changed;
            start.clear();
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ArrayList<Integer> getStart() {
        return start;
    }

    public void setStart(ArrayList<Integer> start) {
        this.start.addAll(start);
//        if(start == null || start.size() < 1){
//            this.start = start;
//        }else{
//            this.start.addAll(start);
//        }

    }


    public ArrayList<Integer> getBlue() {
        return blue;
    }

    public void setBlue(ArrayList<Integer> newBlue) {
        for(int temp : newBlue){
            if(blue.indexOf(temp) == -1){
                blue.add(temp);
            }
        }
    }

    public boolean isChanged() {
        return changed;
    }
    public void setChanged(boolean nowchanged) {
         changed = nowchanged;
        if(changeHandler != null){
            changeHandler.removeCallbacksAndMessages(null);
            changeHandler.postDelayed(resetChange,2000);
        }

    }
}
