package com.zzj.dragfloatprograssview;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.zzj.dragfloatprograssview.adapter.NewsCommentAdapter;
import com.zzj.dragfloatprograssview.bean.NewsCommentBean;
import com.zzj.dragfloatprograssview.view.DragFloatActionView;
import com.zzj.dragfloatprograssview.view.NestedScrollingDetailContainer;
import com.zzj.dragfloatprograssview.view.NestedScrollingWebView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    /**
     * 内容容器 webVIew+评论列表的容器
     */
    protected NestedScrollingDetailContainer nested_container;
    protected RecyclerView recyclerView;
    protected NestedScrollingWebView webView;
    /**
     * 拖动的圆形进度控件
     */
    DragFloatActionView floatActionView;
    /**
     * webView的高度  通过网页返回的
     */
    public float webViewHeight = 0f;
    protected NewsCommentAdapter adapter;
    private List<NewsCommentBean> commentBeans = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatActionView = findViewById(R.id.dfav_view);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NewsCommentAdapter(R.layout.study_item_comment,commentBeans);
        recyclerView.setAdapter(adapter);
        initWebView();
        setupWebView();
        getCommentData();
        /**
         * 监听webView滚动
         */
        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(!floatActionView.isProgressAnimating()){
                    setProgress();
                }
            }
        });

        /**
         * 设置点击事件才可以拖动
         */
        floatActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * 添加webView到recyclerView
     */
    private void initWebView(){
        webView = findViewById(R.id.web_container);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("https://github.com/z1060932884/DragFloatProgressView");
//        webView.loadUrl("http://www.baidu.com");
    }

    public void getCommentData() {
        for (int i = 0; i < 10; i++) {
            NewsCommentBean studyNewsCommentBean = new NewsCommentBean();
            studyNewsCommentBean.setHeaderImage("");
            studyNewsCommentBean.setUsername("张三" + i);
            studyNewsCommentBean.setContent("内容测试" + i);
            studyNewsCommentBean.setUpdateTime(i + "天前");
            //模拟回复数据
            int x = 2 + (int) (Math.random() * 10);
            List<NewsCommentBean.ReplyListBean> replyListBeans = new ArrayList<>();
            for (int j = 0; j < x; j++) {
                NewsCommentBean.ReplyListBean replyListBean = new NewsCommentBean.ReplyListBean();
                replyListBean.setReplayUserName("李四" + j);
                replyListBean.setReplyContent("回复内容测试" + i);
                replyListBeans.add(replyListBean);
            }
            studyNewsCommentBean.setReplyList(replyListBeans);
            commentBeans.add(studyNewsCommentBean);
        }
        adapter.notifyDataSetChanged();
    }
    /**
     * 设置WebView
     */
    private void setupWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:MyApp.resize(document.body.getBoundingClientRect().height)");
                super.onPageFinished(view, url);
            }
        });
        webView.addJavascriptInterface(this, "MyApp");
    }

    @JavascriptInterface
    public void resize(final float height) {
        ((Activity) MainActivity.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webViewHeight = (int) (height * getResources().getDisplayMetrics().density)+50;
                setProgress();
            }
        });
    }
    /**
     * 设置环形进度
     */
    private void setProgress(){
        if (webViewHeight == 0f) {
            return;
        }

        //1、设置圆形滚动总的时间
        floatActionView.setTotalAnimalDuration(30000);
        //2.设置总进度大小
        floatActionView.setMaxProgress((int) webViewHeight);
        //3、每次进度的大小
        int progress =  (floatActionView.getCurrentPosition() + (((int)webViewHeight)/5));
        //
        if (progress > webViewHeight) {
            progress = (int) webViewHeight;
        }
        //设置当前进度
        floatActionView.setCurrentProgress(progress, true);
    }
}
