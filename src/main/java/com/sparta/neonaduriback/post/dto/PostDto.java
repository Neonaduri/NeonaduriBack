package com.sparta.neonaduriback.post.dto;

import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.days.model.Days;
import com.sparta.neonaduriback.post.model.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PostDto {
    private Long postId;
    private String postUUID;
    private String startDate;
    private String endDate;
    private int dateCnt;
    private String postTitle;
    private String location;
    private String postImgeUrl;
    private String theme;
    private boolean islike;
    private int likeCnt;
    private boolean ispublic;
    private int viewCnt;
    private User user;
    private List<DaysDto> days = new ArrayList<>();

//    public PostDto(Post post) {
//        this.postId = post.getPostId();
//        this.postUUID = post.getPostUUID();
//        this.startDate = post.getStartDate();
//        this.endDate = post.getEndDate();
//        this.dateCnt = post.getDateCnt();
//        this.postTitle = post.getPostTitle();
//        this.location = post.getLocation();
//        this.postImgeUrl = post.getPostImgUrl();
//        this.theme = post.getTheme();
//        this.islike = post.isIslike();
//        this.likeCnt = post.getLikeCnt();
//        this.ispublic = post.isIspublic();
//        this.viewCnt = post.getViewCnt();
//        this.user = post.getUser();
//        this.days = post.getDays();
//    }
}