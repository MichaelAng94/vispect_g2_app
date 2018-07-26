package com.vispect.android.vispect_g2_app.utils;

import android.os.Environment;

public class SDcardTools {
	 /**
     * 检查是否存在SDCard
     * @return
     */
    public static boolean hasSdcard(){
            String state = Environment.getExternalStorageState();
            if(state.equals(Environment.MEDIA_MOUNTED)){
                    return true;
            }else{
                    return false;
            }
    }
}
