package com.vispect.android.vispect_g2_app.bean;

/**
 * @author Xu
 * @version 2016年10月11日 下午3:00:38
 * 类说明 :报警代码
 */
public class ADASEventCode {
    public final static int DLD = 10;    //左车道偏离_虚线报警
    public final static int DLF = 11;    // 左车道偏离_实线报警
    public final static int DRD = 12;    //右车道偏离_虚线报警
    public final static int DRF = 13;    // 右车道偏离_实线报警
    public final static int CCH = 14;    // 前方车辆碰撞_危险级别
    public final static int CCL = 15;    // 前方车辆碰撞_小心级别
    public final static int CPH = 16;    // 前方行人碰撞_危险级别
    public final static int CPL = 17;    // 前方行人碰撞_小心级别
    public final static int CCM = 18;    // 前方车辆启动
    public final static int DCE = 19;    // 设备摄像头故障
    public final static int DGE = 20;    // 设备GPS无法正常使用
    public final static int LRNS = 21;   // 无法确认左边还是右边的压线报警
}
