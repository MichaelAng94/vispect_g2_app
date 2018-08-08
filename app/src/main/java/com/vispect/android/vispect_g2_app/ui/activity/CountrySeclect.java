package com.vispect.android.vispect_g2_app.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


import com.gjiazhe.wavesidebar.WaveSideBar;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.adapter.CountrySelectAdapter;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.bean.Country;
import com.vispect.android.vispect_g2_app.utils.CharacterParser;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/***
 * 选择国家或地区的界面
 */

public class CountrySeclect extends BaseActivity {

    @Bind(R.id.side_bar)
    WaveSideBar sideBar;
    @Bind(R.id.countrt_list)
    ListView countrtList;
    ArrayList<Country> countrylist;
    private CharacterParser characterParser = CharacterParser.getInstance();

    private final static Comparator<Object> CHINA_COMPARE = Collator.getInstance(Locale.CHINA);
    private final static Comparator<Object> EN_COMPARE = Collator.getInstance(Locale.ENGLISH);

    @Override
    public int getContentResource() {
        return R.layout.activity_country_seclect;
    }

    @Override
    protected void initView(View view) {
        TextView title = findViewById(R.id.iv_titlebar_title);
        title.setText(STR(R.string.Country_select_title));

        countrylist = new ArrayList<>();
        String[] country;

        country = getResources().getStringArray(R.array.languages_e);
        ArrayList<String> a = (ArrayList<String>) getArrList(country);
        Collections.sort(a, EN_COMPARE);
        for (int i = 0; i < a.size(); i++) {
            Country con = new Country();
            con.setName(a.get(i));
            con.setPingyin(a.get(i));
            countrylist.add(con);

        }


        sideBar.setIndexItems("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");

        countrtList.setAdapter(new CountrySelectAdapter(this, (ArrayList<Country>) countrylist));

        countrtList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message msg = new Message();
                msg.arg2 = 20;
                msg.obj = countrylist.get(position).getName();
                InstallActivity.transHandler.sendMessage(msg);
                finish();
            }
        });


        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String s) {
                for (int i = 0; i < countrylist.size(); i++) {
                    if (s.charAt(0) == countrylist.get(i).getPingyin().toUpperCase().charAt(0)) {
                        countrtList.setSelection(i);
                        break;
                    }
                }
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    private List<String> getArrList(String[] arr) {
        List<String> list = new ArrayList<String>();
        for (String col : arr) {
            list.add(col);
        }
        return list;
    }

    @OnClick(R.id.iv_titlebar_back)
    public void onViewClicked() {
        finish();
    }

    private int getPositonByChar(char ch) {
        for (int i = 0; i < countrylist.size(); i++) {
            if (countrylist.get(i).getPingyin().charAt(0) == ch) {
                return i;
            }
        }
        return -1;
    }
}
