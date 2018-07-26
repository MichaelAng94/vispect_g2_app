package com.vispect.android.vispect_g2_app.bean;

/**
 * OBD自动破解的bit位信息
 *
 * Created by xu on 2017/8/31.
 */
public class OBDAutoCrackElement {
    private String id;   //value的id
    private int value;   //位的值
    private int index;   //位于第几个位
    private int count;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "OBDAutoCrackElement{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", index=" + index +
                ", count=" + count +
                '}';
    }
}
