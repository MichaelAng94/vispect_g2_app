package com.vispect.android.vispect_g2_app.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.SelectCarBrandOrModelAdapter;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.app.AppManager;
import com.vispect.android.vispect_g2_app.bean.CarInfo;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.OnClickYesOrNoListener;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.ui.widget.InputCarModeDialog;
import com.vispect.android.vispect_g2_app.ui.widget.SideBar;
import com.vispect.android.vispect_g2_app.utils.CharacterParser;
import com.vispect.android.vispect_g2_app.utils.PinyinComparator;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;
import com.vispect.android.vispect_g2_app.utils.XuView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * 选择车辆型号
 *
 * Created by xu on 2017/2/23.
 */
public class SelectCarModelActivity extends BaseActivity {

    private static final String TAG = "SelectCarModelActivity";

    @Bind(R.id.sortListView)
    ListView sortListView;
    @Bind(R.id.sideBar)
    SideBar sideBar;
    @Bind(R.id.tv_dialog)
    TextView dialog;
    @Bind(R.id.et_search)
    EditText mClearEditText;
    @Bind(R.id.tv_cancel)
    TextView tv_cancel;
    @Bind(R.id.iv_titlebar_title)
    TextView title;
//    @Bind(R.id.iv_titlebar_save_frame)
//    TextView iv_titlebar_add;

    private SelectCarBrandOrModelAdapter adapter;


    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<CarInfo> SourceDateList=new ArrayList<CarInfo>();

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private String nowBrand = "";

    @Override
    public int getContentResource() {
            return R.layout.activity_carbrandormodel;
    }

    @Override
    protected void initView(View view) {
        title.setText(STR(R.string.select_model_title));
//        XuView.setViewVisible(iv_titlebar_add, true);
//        iv_titlebar_add.setText(STR(R.string.add));
//        iv_titlebar_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                UIHelper.showAutoCrack(SelectCarModelActivity.this);
//                showDialogToInputBrand(SelectCarModelActivity.this, STR(R.string.obd_auto_crack_tips_save), new OnClickYesOrNoListener() {
//                    @Override
//                    public void isyes(boolean var1, DialogInterface dialog) {
//                        if (var1) {
//                            AppContext.getInstance().setNowBrand(builder.getBrand());
//                            AppContext.getInstance().setNowModel(builder.getMode());
//                            AppManager.getInstance().finishActivity(SelectCarBrandActivity.class);
//                            dialog.dismiss();
//                            finish();
//                        }else{
//                            dialog.dismiss();
//                        }
//
//                    }
//                });
//            }
//        });
//        try {
//            nowBrand=getIntent().getStringExtra(ARG.CARBRAND);
//        } catch (Exception e) {
//            // TODO: handle exception
//            XuToast.show(SelectCarModelActivity.this, STR(R.string.cube_views_load_more_error));
//            finish();
//            return;
//        }

        initview();
        getModel(AppContext.getInstance().getNowBrand());
    }



    private InputCarModeDialog.Builder builder;
    private static DialogInterface askDialog;
    public DialogInterface showDialogToInputBrand(Context context, String msg, final OnClickYesOrNoListener listener) {


        try{
            if(askDialog != null){
                askDialog.dismiss();
            }
            builder = new InputCarModeDialog.Builder(context);
            builder.setMessage(msg);
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askDialog = dialog;
                    listener.isyes(true,dialog);

                }
            });

            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    askDialog = dialog;
                    listener.isyes(false,dialog);
                }
            });

            builder.create().show();
            return askDialog;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    @OnClick(R.id.tv_cancel)
     void clearEdit(){
        mClearEditText.setText("");
    }

    private void initview() {
        // TODO Auto-generated method stub
        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                if (SourceDateList.size() > 0) {
                    int position = adapter.getPositionForSection(s.charAt(0));
                    if (position != -1) {
                        sortListView.setSelection(position);
                    }
                }
            }
        });

        sortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                if (true) {
                    if (!XuString.isEmpty(SourceDateList.get(position).getModel())) {
                        //在这里跳出

                            AppContext.getInstance().setNowModel(SourceDateList.get(position).getModel());
                            AppManager.getInstance().finishActivity(SelectCarBrandActivity.class);
                            finish();


                    }
                } else {
                    XuToast.show(SelectCarModelActivity.this, STR(R.string.road_live_notconnect));
                }


            }
        });

        mClearEditText.clearFocus();
        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                filterData(s.toString());
                XuView.setViewVisible(tv_cancel, !XuString.isEmpty(s.toString()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }


    /**
     * 排列数据
     */

    private void paixu(){
        try {
        //获得名字的首字母
        for(int i=0;i<SourceDateList.size();i++){
            String temp_model = SourceDateList.get(i).getModel();
//            String str=getPYIndexStr(temp_model, true);
//            String str=XuString.getShouZiMu(temp_model);

                String str = PinyinHelper.getShortPinyin(temp_model).toUpperCase();
                SourceDateList.get(i).setSortLetters(str.substring(0, 1));

        }

        }catch (Exception e){
            e.printStackTrace();

        }

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        adapter = new SelectCarBrandOrModelAdapter(this, SourceDateList);
        adapter.setIsgetBrand(false);
        sortListView.setAdapter(adapter);

    }

    private void getModel(String brand){

        AppApi.getbrandormodel(2, brand, new ResultCallback<ResultData<ArrayList<CarInfo>>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                XuLog.e(TAG, "获取品牌车型信息失败：" + e.getMessage());
            }

            @Override
            public void onResponse(ResultData<ArrayList<CarInfo>> response) {
                XuLog.e(TAG, "获取品牌车型信息成功：" + response.getMsg().toString());
                SourceDateList.addAll(response.getMsg());
                paixu();
            }
        });
    }


    /**
     * 为ListView填充数据
     * @param date
     * @return
     */
    private List<CarInfo> filledData(String[] date){
        List<CarInfo> mSortList = new ArrayList<CarInfo>();

        for(int i=0; i<date.length; i++){
            CarInfo sortModel = new CarInfo();
            sortModel.setModel(date[i]);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if(sortString.matches("[A-Z]")){
                sortModel.setSortLetters(sortString.toUpperCase());
            }else{
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CarInfo> filterDateList = new ArrayList<CarInfo>();

        if (XuString.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (CarInfo sortModel : SourceDateList) {
                String name = sortModel.getModel();
                if (name.toUpperCase().indexOf(
                        filterStr.toString().toUpperCase()) != -1
                        || characterParser.getSelling(name).toUpperCase()
                        .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        if(filterDateList != null && !filterDateList.isEmpty()){
            adapter.updateListView(filterDateList);
        }

    }
    /**

     * 返回首字母

     * @param strChinese

     * @param bUpCase

     * @return

     */

    public static String getPYIndexStr(String strChinese, boolean bUpCase){

        try{

            StringBuffer buffer = new StringBuffer();

            byte b[] = strChinese.getBytes("GBK");//把中文转化成byte数组

            for(int i = 0; i < b.length; i++){

                if((b[i] & 255) > 128){

                    int char1 = b[i++] & 255;

                    char1 <<= 8;//左移运算符用“<<”表示，是将运算符左边的对象，向左移动运算符右边指定的位数，并且在低位补零。其实，向左移n位，就相当于乘上2的n次方

                    int chart = char1 + (b[i] & 255);

                    buffer.append(getPYIndexChar((char)chart, bUpCase));

                    continue;

                }

                char c = (char)b[i];

                if(!Character.isJavaIdentifierPart(c))//确定指定字符是否可以是 Java 标识符中首字符以外的部分。

                    c = 'A';

                buffer.append(c);

            }

            return buffer.toString();

        }catch(Exception e){

            System.out.println((new StringBuilder()).append("\u53D6\u4E2D\u6587\u62FC\u97F3\u6709\u9519").append(e.getMessage()).toString());

        }

        return null;

    }

    /**

     * 得到首字母

     * @param strChinese

     * @param bUpCase

     * @return

     */

    private static char getPYIndexChar(char strChinese, boolean bUpCase){

        int charGBK = strChinese;

        char result;

        if(charGBK >= 45217 && charGBK <= 45252)

            result = 'A';

        else

        if(charGBK >= 45253 && charGBK <= 45760)

            result = 'B';

        else

        if(charGBK >= 45761 && charGBK <= 46317)

            result = 'C';

        else

        if(charGBK >= 46318 && charGBK <= 46825)

            result = 'D';

        else

        if(charGBK >= 46826 && charGBK <= 47009)

            result = 'E';

        else

        if(charGBK >= 47010 && charGBK <= 47296)

            result = 'F';

        else

        if(charGBK >= 47297 && charGBK <= 47613)

            result = 'G';

        else

        if(charGBK >= 47614 && charGBK <= 48118)

            result = 'H';

        else

        if(charGBK >= 48119 && charGBK <= 49061)

            result = 'J';

        else

        if(charGBK >= 49062 && charGBK <= 49323)

            result = 'K';

        else

        if(charGBK >= 49324 && charGBK <= 49895)

            result = 'L';

        else

        if(charGBK >= 49896 && charGBK <= 50370)

            result = 'M';

        else

        if(charGBK >= 50371 && charGBK <= 50613)

            result = 'N';

        else

        if(charGBK >= 50614 && charGBK <= 50621)

            result = 'O';

        else

        if(charGBK >= 50622 && charGBK <= 50905)

            result = 'P';

        else

        if(charGBK >= 50906 && charGBK <= 51386)

            result = 'Q';

        else

        if(charGBK >= 51387 && charGBK <= 51445)

            result = 'R';

        else

        if(charGBK >= 51446 && charGBK <= 52217)

            result = 'S';

        else

        if(charGBK >= 52218 && charGBK <= 52697)

            result = 'T';

        else

        if(charGBK >= 52698 && charGBK <= 52979)

            result = 'W';

        else

        if(charGBK >= 52980 && charGBK <= 53688)

            result = 'X';

        else

        if(charGBK >= 53689 && charGBK <= 54480)

            result = 'Y';

        else

        if(charGBK >= 54481 && charGBK <= 55289)

            result = 'Z';

        else

            result = (char)(65 + (new Random()).nextInt(25));

        if(!bUpCase)

            result = Character.toLowerCase(result);

        return result;

    }
    @OnClick(R.id.iv_titlebar_back)
    void back() {
        finish();
    }


}
