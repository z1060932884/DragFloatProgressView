package com.zzj.dragfloatprograssview.bean;

import java.io.Serializable;
import java.util.List;

/**
 * @author : zzj
 * @e-mail : zhangzhijun@pansoft.com
 * @date : 2019/6/3 10:42
 * @desc : 评论bean
 * @version: 1.0
 */
public class NewsCommentBean implements Serializable {

    private String headerImage;
    private String username;
    private String content;
    private String updateTime;
    private List<ReplyListBean> replyList;
    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<ReplyListBean> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<ReplyListBean> replyList) {
        this.replyList = replyList;
    }

    public static class ReplyListBean {
        /**
         * replyTime : 2017-06-24 17:23:40
         * replayUserId : b2f1f00a615941588e85f927dc8620a0
         * replayUserName : heyanmin
         * id : 8ab8c3cb0e2248f08aded1ed97959014
         * replyContent : 你好
         * userName : 砳砳007
         * userId : 70c09944c0544a8c98ab5e6e94e3d452
         */
        private String replyTime;
        private String replayUserId;
        private String replayUserName;
        private String id;
        private String replyContent;
        private String userName;
        private String userId;

        public String getReplyTime() {
            return replyTime;
        }

        public void setReplyTime(String replyTime) {
            this.replyTime = replyTime;
        }

        public String getReplayUserId() {
            return replayUserId;
        }

        public void setReplayUserId(String replayUserId) {
            this.replayUserId = replayUserId;
        }

        public String getReplayUserName() {
            return replayUserName;
        }

        public void setReplayUserName(String replayUserName) {
            this.replayUserName = replayUserName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getReplyContent() {
            return replyContent;
        }

        public void setReplyContent(String replyContent) {
            this.replyContent = replyContent;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }
}
