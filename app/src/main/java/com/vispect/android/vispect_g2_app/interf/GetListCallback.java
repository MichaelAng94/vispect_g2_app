package com.vispect.android.vispect_g2_app.interf;

import java.util.List;

public interface GetListCallback<T> {
   void onGetListSuccess(List<T> list);
   void onGetListFailed();
}
