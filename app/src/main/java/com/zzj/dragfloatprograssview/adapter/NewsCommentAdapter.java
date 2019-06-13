package com.zzj.dragfloatprograssview.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zzj.dragfloatprograssview.R;
import com.zzj.dragfloatprograssview.bean.NewsCommentBean;
import com.zzj.dragfloatprograssview.view.NestFullListView;
import com.zzj.dragfloatprograssview.view.NestFullListViewAdapter;
import com.zzj.dragfloatprograssview.view.NestFullViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : zzj
 * @e-mail : zhangzhijun@pansoft.com
 * @date : 2019/6/3 10:41
 * @desc :
 * @version: 1.0
 */
public class NewsCommentAdapter extends BaseQuickAdapter<NewsCommentBean, BaseViewHolder> {

    public NewsCommentAdapter( int layoutResId, @Nullable List<NewsCommentBean> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, NewsCommentBean item) {
        helper.setText(R.id.tv_name,item.getUsername());
        helper.setText(R.id.tv_comment_item,item.getContent());
        helper.setText(R.id.tv_comment_time,item.getUpdateTime());
        NestFullListView fullListView = helper.getView(R.id.nflv_list);
        initReplyList(fullListView,item);
    }

    /**
     * 加载回复列表
     * @param fullListView
     * @param item
     */
    private void initReplyList(NestFullListView fullListView, NewsCommentBean item) {
        //显示子布局评论
        fullListView.setVisibility(View.VISIBLE);
        // 加载查看更多评论View
        View more_comment = LayoutInflater.from(mContext).inflate(R.layout.study_news_details_comment_check_foot_view, null);
        initReplyListAdapter(fullListView,more_comment,item);
    }

    /**
     * 初始化子评论的适配器
     * @param fullListView
     * @param more_comment
     * @param item
     */
    private void initReplyListAdapter(final NestFullListView fullListView, final View more_comment, final NewsCommentBean item) {
        // 子布局评论中的所有数据
        final List<NewsCommentBean.ReplyListBean> itemAllCommentList = item.getReplyList();
        //向子布局显示的view NestFullListView适配器的数据源
        final List<NewsCommentBean.ReplyListBean> itemBeans = new ArrayList<>();
        // 对评论的数据进行判断加载，大于3个显示查看更多评论，小于三个直接显示
            if (itemAllCommentList.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    itemBeans.add(itemAllCommentList.get(i));
                }
                //加载显示更多评论布局
                if(fullListView.getFooterCount() == 0){
                    fullListView.setFooterView(3, more_comment);
                }
            } else {
                itemBeans.addAll(itemAllCommentList);
            }

        //初始化子评论列表适配器
        final NestFullListViewAdapter<NewsCommentBean.ReplyListBean> nestFullListViewAdapter
                = new NestFullListViewAdapter<NewsCommentBean.ReplyListBean>
                (R.layout.study_item_news_details_common_child_list, itemBeans) {
            @Override
            public void onBind(int i, NewsCommentBean.ReplyListBean itemHotComment, NestFullViewHolder nestFullViewHolder) {
                //评论回复人
                nestFullViewHolder.setText(R.id.txt_name, itemHotComment.getReplayUserName());
                //评论回复的内容
                nestFullViewHolder.setText(R.id.txt_content, itemHotComment.getReplyContent());
            }
        };
        fullListView.setAdapter(nestFullListViewAdapter);
        /**
         * 子评论的点击事件
         */
        fullListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        fullListView.setOnItemClickListener(new NestFullListView.OnItemClickListener() {
            @Override
            public void onItemClick(NestFullListView parent, View view, int position) {
                ToastUtils.showShort("子评论item点击事件+"+item.getReplyList().get(position).getReplyContent());
            }
        });
    }
}
