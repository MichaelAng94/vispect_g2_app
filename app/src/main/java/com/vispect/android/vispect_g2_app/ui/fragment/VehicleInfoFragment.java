package com.vispect.android.vispect_g2_app.ui.fragment;

/**
 * Created by mo on 2018/3/27.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.ui.activity.CountrySeclect;
import com.vispect.android.vispect_g2_app.ui.activity.InstallActivity;
import com.vispect.android.vispect_g2_app.ui.activity.SelectCarBrandActivity;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.weigan.loopview.LoopView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by mo on 2018/3/27.
 */


public class VehicleInfoFragment extends BaseFragment {
    private static final String TAG = "VehicleInfoFragment";

    ArrayList<String> arrayList;

    @Bind(R.id.tv_mine_userifo_tips1)
    TextView tvMineUserifoTips1;
    @Bind(R.id.tv_region)
    TextView tv_region;
    @Bind(R.id.tv_car_year)
    TextView tv_car_year;
    @Bind(R.id.edt_car_brand)
    EditText edt_car_brand;
    @Bind(R.id.edt_car_model)
    EditText edt_car_model;
    @Bind(R.id.select_install)
    TextView selectInstall;
    @Bind(R.id.ll_mask)
    LinearLayout ll_mask;
    @Bind(R.id.loopView)
    LoopView loopView;
    @Bind(R.id.select_year)
    LinearLayout selectYear;
    @Bind(R.id.calibration4fragment)
    FrameLayout calibration4fragment;
    private Button save;
    private TextView title;
    private TextView titleFinish;
    PopupWindow popupWindow;
    public static Handler mHandler = new Handler();


    private Runnable requiredIsEmpty = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            XuToast.show(getActivity(), STR(R.string.required_cant_not_empty));
        }
    };

    private Runnable savefial = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            XuToast.show(getActivity(), STR(R.string.save_fail));
        }
    };
    private Runnable savesuccess = new Runnable() {
        @Override
        public void run() {
            hideProgress();
            XuToast.show(getActivity(), STR(R.string.save_success));
            Message msg = new Message();
            msg.arg2 = 5;
            InstallActivity.transHandler.sendMessage(msg);
        }
    };


    @Override
    public int getContentResource() {
        return R.layout.fragment_calibration4step1;
    }

    class onclick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            UIHelper.showAsk(getActivity(), STR(R.string.ask_save_data), true, new OnClickYesOrNoListener() {
                @Override
                public void isyes(boolean b, DialogInterface dialog) {
                    if (b) {
                        saveData();
                    }
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }



    @Override
    protected void initView(View view) {
        save =  getActivity().findViewById(R.id.btn_save);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date date = new Date();
        arrayList = new ArrayList<>();


        for (int x = Integer.parseInt(sdf.format(date)); x >= 1980; x--) {
            arrayList.add(x + "");
        }

        loopView.setItems(arrayList);
        loopView.setInitPosition(0);
        // 滚动监听



//        titleFinish.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveData(v);
//            }
//        });

        edt_car_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.startActivity(getActivity(), SelectCarBrandActivity.class);
            }
        });
//        edt_car_model.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UIHelper.showSelectCarModelForResult(getActivity(), ARG.REQUEST_CARBRAND, AppContext.getInstance().getNowBrand());
//            }
//        });

        if (!AppConfig.getInstance(AppContext.getInstance()).getUserId().isEmpty()) {
            if (AppConfig.getInstance(AppContext.getInstance()).getUserId().equals(AppConfig.getInstance(AppContext.getInstance()).getLastuid())) {
                tv_region.setText(AppConfig.getInstance(AppContext.getInstance()).getLasregion());
                tv_car_year.setText(AppConfig.getInstance(AppContext.getInstance()).getLascaryear());
                edt_car_brand.setText(AppContext.getInstance().getNowBrand());
                edt_car_model.setText(AppContext.getInstance().getNowModel());
            }
        }

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                tv_region.setText(msg.obj.toString());
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    void showPopupWindowbyShowActivity(String a, String b, String c) {
        final View contentView = LayoutInflater.from(AppContext.getInstance()).inflate(R.layout.popupwindow_select_activity_menu, null);
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ll_mask.setVisibility(View.GONE);
            }
        });

        TextView tv_menua = (TextView) contentView.findViewById(R.id.tv_menua);
        TextView tv_menub = (TextView) contentView.findViewById(R.id.tv_menub);
        TextView tv_menuc = (TextView) contentView.findViewById(R.id.tv_menuc);
        tv_menua.setText(a);
        tv_menub.setText(b);
        tv_menuc.setText(c);

        contentView.findViewById(R.id.ll_menua).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInstall.setText(R.string.calibration4list_step1_context1);
                popupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.ll_menub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInstall.setText(R.string.calibration4list_step1_context2);
                popupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.ll_menuc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectInstall.setText(R.string.calibration4list_step1_context3);
                popupWindow.dismiss();
            }
        });

        contentView.findViewById(R.id.ll_popuwindow_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ll_mask.setVisibility(View.VISIBLE);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(getActivity().findViewById(R.id.calibration4fragment), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }


    void saveData() {
        if (AppConfig.getInstance(AppContext.getInstance()).getUserId().isEmpty()) {
            return;
        }
        String region = tv_region.getText().toString().trim();
        String car_year = tv_car_year.getText().toString().trim();
        String car_brand = edt_car_brand.getText().toString().trim();
        String car_model = edt_car_model.getText().toString().trim();
        if (!region.isEmpty() && !car_year.isEmpty() && !car_brand.isEmpty() && !car_model.isEmpty()) {
            AppContext.getInstance().setNowBrand(car_brand);
            AppContext.getInstance().setNowModel(car_model);
            AppConfig.getInstance(AppContext.getInstance()).setLastuid(AppConfig.getInstance(AppContext.getInstance()).getUserId());
            AppConfig.getInstance(AppContext.getInstance()).setLasregion(region);
            AppConfig.getInstance(AppContext.getInstance()).setLascaryear(car_year);
            mHandler.post(savesuccess);
        } else {
            mHandler.post(requiredIsEmpty);
        }


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        save.setOnClickListener(new onclick());
        edt_car_brand.setText(AppContext.getInstance().getNowBrand().equals("    ") ? "" : AppContext.getInstance().getNowBrand());
        edt_car_model.setText(AppContext.getInstance().getNowModel().equals("    ") ? "" : AppContext.getInstance().getNowModel());
    }

    public void changeCountry(String country){
        tv_region.setText(country);
    }

    @OnClick({R.id.btn_cancle, R.id.btn_get, R.id.select_install, R.id.tv_car_year, R.id.ll_mask, R.id.tv_region})
    public void onViewClicked(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        switch (view.getId()) {
            case R.id.tv_region:
                UIHelper.startActivity(getActivity(), CountrySeclect.class);
                break;
            case R.id.ll_mask:
                selectYear.setVisibility(View.GONE);
                ll_mask.setVisibility(View.GONE);
                break;
            case R.id.btn_cancle:
                selectYear.setVisibility(View.GONE);
                ll_mask.setVisibility(View.GONE);
                break;
            case R.id.btn_get:
                tv_car_year.setText(arrayList.get(loopView.getSelectedItem()));
                selectYear.setVisibility(View.GONE);
                ll_mask.setVisibility(View.GONE);
                break;
            case R.id.select_install:
                showPopupWindowbyShowActivity(STR(R.string.calibration4list_step1_context1), STR(R.string.calibration4list_step1_context2), STR(R.string.calibration4list_step1_context3));
                break;
            case R.id.tv_car_year:
                selectYear.setVisibility(View.VISIBLE);
                ll_mask.setVisibility(View.VISIBLE);
                break;
        }
    }
}

