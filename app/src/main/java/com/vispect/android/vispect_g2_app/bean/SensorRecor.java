package com.vispect.android.vispect_g2_app.bean;

/**
 *  一条sensor的历史信息
 *
 * Created by xu on 2016/12/12.
 */
public class SensorRecor {
    private int id;
    private String time;
    private float x;
    private float y;
    private float z;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "SensorRecor{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
