package com.vispect.android.vispect_g2_app.ui.widget;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.interf.CarDialogClickListener;
import com.vispect.android.vispect_g2_app.interf.DialogClickListener;

import java.util.ArrayList;

/**
 * Created by mo on 2018/7/3.
 */

public class DialogHelp {

    private static MaterialDialog mMaterialDialog;
    private static DialogHelp dialogHelp;
    private static ArrayList<MaterialDialog> dialoglist = new ArrayList<>();
    private MaterialDialog.Builder mBuilder;
    private Activity ac;

    public static DialogHelp getInstance() {
        if (dialogHelp == null) {
            dialogHelp = new DialogHelp();
        }
        if (mMaterialDialog != null) {
            dialoglist.add(mMaterialDialog);
        }
        return dialogHelp;
    }

    public void hideDialog() {
        if (mMaterialDialog != null && !ac.isFinishing()) {
            mMaterialDialog.dismiss();
            mMaterialDialog = null;
        } else if (dialoglist.size() > 0) {
            dialoglist.get(dialoglist.size() - 1).dismiss();
            dialoglist.remove(dialoglist.size() - 1);
        }
    }

    public void loginDialog(Activity ac) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = mBuilder.title("Logging in")
                .canceledOnTouchOutside(false)
                .content(AppContext.getInstance().getResources().getString(R.string.waitting))
                .progress(true, 0)
                .show();

    }

    public MaterialDialog editDialog(Activity ac, String title, final DialogClickListener listener) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = new MaterialDialog.Builder(ac)
                .customView(R.layout.editdialog, false)
                .show();
        final EditText et = (EditText) mMaterialDialog.findViewById(R.id.et_phone);
        TextView titleDialog = (TextView) mMaterialDialog.findViewById(R.id.tv_title);
        titleDialog.setText(title);
        Button btnOk = (Button) mMaterialDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.clickYes(et.getText().toString());
            }
        });
        Button btnCancle = (Button) mMaterialDialog.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
        return mMaterialDialog;
    }

    public MaterialDialog carDialog(Activity ac, String title, final CarDialogClickListener listener) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = new MaterialDialog.Builder(ac)
                .customView(R.layout.cardialog, false)
                .show();
        final EditText et_deiver = (EditText) mMaterialDialog.findViewById(R.id.et_brand);
        final EditText et_model = (EditText) mMaterialDialog.findViewById(R.id.et_model);
        TextView titleDialog = (TextView) mMaterialDialog.findViewById(R.id.tv_title_dialog);
        titleDialog.setText(title);
        Button btnOk = (Button) mMaterialDialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.clickYes(et_deiver.getText().toString(), et_model.getText().toString());
            }
        });
        Button btnCancle = (Button) mMaterialDialog.findViewById(R.id.btn_cancel);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
        return mMaterialDialog;
    }

    public void connectDialog(Activity ac, String title) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = mBuilder.title(title)
                .canceledOnTouchOutside(false)
                .content(ac.getResources().getString(R.string.waitting))
                .progress(true, 0)
                .show();
    }

    public MaterialDialog connectDialog(Activity ac, String title, String context) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = mBuilder.title(title)
                .canceledOnTouchOutside(false)
                .content(context)
                .progress(true, 0)
                .show();
        return mMaterialDialog;
    }

    public void chooesDialog(Activity ac, String title) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = mBuilder.title(title)
                .canceledOnTouchOutside(false)
                .content(ac.getResources().getString(R.string.waitting))
                .progress(true, 0)
                .show();
    }

    /**
     * @param defaultValue 0表示默认选中男,1表示女
     * @param listener
     */

    public void sexDialog(Activity ac, int defaultValue, final DialogClickListener listener) {
        this.ac = ac;
        mBuilder = new MaterialDialog.Builder(ac);
        mMaterialDialog = new MaterialDialog.Builder(ac)
                .customView(R.layout.dialog_sex, false)
                .show();
        final RadioGroup rg = (RadioGroup) mMaterialDialog.findViewById(R.id.ra_sex);
        rg.check(defaultValue == 0 ? R.id.male : R.id.female);
        Button btnOk = (Button) mMaterialDialog.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) mMaterialDialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMaterialDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rg.getCheckedRadioButtonId() == R.id.male) {
                    listener.clickYes("0");
                } else {
                    listener.clickYes("1");
                }

            }
        });
    }


}
