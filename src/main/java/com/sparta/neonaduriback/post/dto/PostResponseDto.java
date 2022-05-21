package com.sparta.neonaduriback.post.dto;//package com.sparta.neonaduri_back.post.dto;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.data.domain.Page;
//
//import java.util.List;
//
//@Getter
//@Setter
//public class PostResponseDto {
//    private List<PostListDto> myplanList;
//    private int totalPage;
//    private boolean islastPage;
//
//    public PostResponseDto(Page<PostListDto> getMyPosts, boolean islastPage) {
//        this.myplanList = getMyPosts.getContent();
//        this.totalPage = getMyPosts.getTotalPages();
//        this.islastPage = islastPage;
//
//    }
//}