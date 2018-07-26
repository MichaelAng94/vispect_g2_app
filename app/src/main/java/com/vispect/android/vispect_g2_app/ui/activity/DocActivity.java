package com.vispect.android.vispect_g2_app.ui.activity;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.vispect.android.vispect_g2_app.R;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 文档查看
 * <p>
 * Created by xu on 2016/12/23.
 */
public class DocActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener, OnDrawListener, OnPageScrollListener, OnErrorListener {

    @Bind(R.id.pdfView)
    PDFView pdfView;

    @Override
    public int getContentResource() {
        return R.layout.activity_doc_calibration;
    }

    @Override
    protected void initView(View view) {
        displayFromAssets("useguidefors_en.pdf");
    }


    /**
     * 从asset目录读取PDF文件
     *
     * @param assetFileName
     */
    private void displayFromAssets(String assetFileName) {
        pdfView.fromAsset(assetFileName)
//                .pages(0, 2, 1, 3, 3, 3)  //过滤掉某些页数
                .enableSwipe(true)          //是否允许翻页，默认是允许翻页
                .swipeHorizontal(false)   //pdf文档翻页是否是水平翻页，默认是左右滑动翻页
                .enableDoubletap(true)
                .defaultPage(0)       //设置默认显示页
                .onDraw(this)        //绘图监听
                .onLoad(this)        //设置加载监听
                .onPageChange(this)   //设置翻页监听
                .onPageScroll(this)
                .onError(this)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .spacing(15)
                .load();
    }


    /**
     * 、
     * 直接根据文件路径读取PDF文件
     *
     * @param file
     */
    private void displayFromFile(File file) {
        pdfView.fromFile(file)
//                .pages(0, 2, 1, 3, 3, 3)  //过滤掉某些页数
                .enableSwipe(true)          //是否允许翻页，默认是允许翻页
                .swipeHorizontal(false)   //pdf文档翻页是否是水平翻页，默认是左右滑动翻页
                .enableDoubletap(true)
                .defaultPage(0)       //设置默认显示页
                .onDraw(this)        //绘图监听
                .onLoad(this)        //设置加载监听
                .onPageChange(this)   //设置翻页监听
                .onPageScroll(this)
                .onError(this)
                .enableAnnotationRendering(false)
                .password(null)
                .scrollHandle(null)
                .load();
    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

    }

    @Override
    public void onPageScrolled(int page, float positionOffset) {

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.img_back_main)
    public void onViewClicked() {
        finish();
    }
}
