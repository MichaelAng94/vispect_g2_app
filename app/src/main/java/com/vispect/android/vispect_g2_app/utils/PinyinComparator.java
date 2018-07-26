package com.vispect.android.vispect_g2_app.utils;

import com.vispect.android.vispect_g2_app.bean.CarInfo;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CarInfo> {
    public int compare(CarInfo o1, CarInfo o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }


}
