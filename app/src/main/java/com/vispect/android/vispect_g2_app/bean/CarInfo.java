package com.vispect.android.vispect_g2_app.bean;

/**
 * 车辆信息
 *
 * Created by xu on 2017/2/21.
 */
public class CarInfo {
    private String brand;
    private String model;
    private long length;
    private long width;
    private long height;
    private String sortLetters;
    private String isreviewed = "0";  //是否已审核   0：否   1：是

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getIsreviewed() {
        return isreviewed;
    }

    public void setIsreviewed(String isreviewed) {
        this.isreviewed = isreviewed;
    }

    @Override
    public String toString() {
        return "CarInfo{" +
                "brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", length=" + length +
                ", width=" + width +
                ", height=" + height +
                ", sortLetters='" + sortLetters + '\'' +
                ", isreviewed='" + isreviewed + '\'' +
                '}';
    }
}
