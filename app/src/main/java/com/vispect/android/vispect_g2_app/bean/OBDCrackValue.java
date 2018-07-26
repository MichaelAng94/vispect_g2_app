package com.vispect.android.vispect_g2_app.bean;


import com.vispect.android.vispect_g2_app.utils.CodeUtil;

/**
 * 打包好的OBD破解信息
 *
 * Created by xu on 2017/1/14.
 */
public class OBDCrackValue {
    byte[] 	m_LightID = new byte[4];		             //灯类ID.
    byte		m_RLight;		                         //右转向
    byte		m_LLight;			                     //左转向
    byte		m_BigLight;		                         //远灯
    byte		m_Light;			                     //行车灯
    byte[]	m_WipperID = new byte[4];		             //雨刮ID.
    byte		m_Wipper;		                         //雨刮
    byte[]	m_ReverseID = new byte[4];		             //倒车ID
    byte		m_Reverse;		                         //倒车
    byte[]	m_BrakeID = new byte[4];		             //刹车ID
    byte		m_Brake;			                     //刹车


    public byte[] getM_LightID() {
        return m_LightID;
    }

    public void setM_LightID(byte[] m_LightID) {
        this.m_LightID = m_LightID;
    }

    public byte getM_RLight() {
        return m_RLight;
    }

    public void setM_RLight(byte m_RLight) {
        this.m_RLight = m_RLight;
    }

    public byte getM_LLight() {
        return m_LLight;
    }

    public void setM_LLight(byte m_LLight) {
        this.m_LLight = m_LLight;
    }

    public byte getM_BigLight() {
        return m_BigLight;
    }

    public void setM_BigLight(byte m_BigLight) {
        this.m_BigLight = m_BigLight;
    }

    public byte getM_Light() {
        return m_Light;
    }

    public void setM_Light(byte m_Light) {
        this.m_Light = m_Light;
    }

    public byte[] getM_WipperID() {
        return m_WipperID;
    }

    public void setM_WipperID(byte[] m_WipperID) {
        this.m_WipperID = m_WipperID;
    }

    public byte getM_Wipper() {
        return m_Wipper;
    }

    public void setM_Wipper(byte m_Wipper) {
        this.m_Wipper = m_Wipper;
    }

    public byte[] getM_ReverseID() {
        return m_ReverseID;
    }

    public void setM_ReverseID(byte[] m_ReverseID) {
        this.m_ReverseID = m_ReverseID;
    }

    public byte getM_Reverse() {
        return m_Reverse;
    }

    public void setM_Reverse(byte m_Reverse) {
        this.m_Reverse = m_Reverse;
    }

    public byte[] getM_BrakeID() {
        return m_BrakeID;
    }

    public void setM_BrakeID(byte[] m_BrakeID) {
        this.m_BrakeID = m_BrakeID;
    }

    public byte getM_Brake() {
        return m_Brake;
    }

    public void setM_Brake(byte m_Brake) {
        this.m_Brake = m_Brake;
    }


/**
 *将OBD的破解数据装成16进制的字符串
  */
    public String toHexStr(){
        byte[] value = new byte[23];
        value[0] = m_LightID[0];
        value[1] = m_LightID[1];
        value[2] = m_LightID[2];
        value[3] = m_LightID[3];
        value[4] = m_RLight;
        value[5] = m_LLight;
        value[6] = m_BigLight;
        value[7] = m_Light;
        value[8] = m_WipperID[0];
        value[9] = m_WipperID[1];
        value[10] = m_WipperID[2];
        value[11] = m_WipperID[3];
        value[12] = m_Wipper;
        value[13] = m_ReverseID[0];
        value[14] = m_ReverseID[1];
        value[15] = m_ReverseID[2];
        value[16] = m_ReverseID[3];
        value[17] = m_Reverse;
        value[18] = m_BrakeID[0];
        value[19] = m_BrakeID[1];
        value[20] = m_BrakeID[2];
        value[21] = m_BrakeID[3];
        value[22] =m_Brake;
        return CodeUtil.bytesToString(value);
    }
}
