package com.vispect.android.vispect_g2_app.bean;
/** 
* @author Xu 
* @version 2017年9月13日 下午5:13:26 
* 类说明 : V款的GPS卫星信息
*/
public class Satellite_V {
    boolean hasAlmanac;
    boolean hasephemeris;
    boolean beuse;
    float azimuth;
    float SNR;
    float hight;
    long TNC;

    public boolean isHasAlmanac() {
        return hasAlmanac;
    }

    public void setHasAlmanac(boolean hasAlmanac) {
        this.hasAlmanac = hasAlmanac;
    }

    public boolean isHasephemeris() {
        return hasephemeris;
    }

    public void setHasephemeris(boolean hasephemeris) {
        this.hasephemeris = hasephemeris;
    }

    public boolean isBeuse() {
        return beuse;
    }

    public void setBeuse(boolean beuse) {
        this.beuse = beuse;
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public float getSNR() {
        return SNR;
    }

    public void setSNR(float SNR) {
        this.SNR = SNR;
    }

    public float getHight() {
        return hight;
    }

    public void setHight(float hight) {
        this.hight = hight;
    }

    public long getTNC() {
        return TNC;
    }

    public void setTNC(long TNC) {
        this.TNC = TNC;
    }
}
