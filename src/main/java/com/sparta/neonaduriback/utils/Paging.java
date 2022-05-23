package com.sparta.neonaduriback.utils;


import com.sparta.neonaduriback.post.dto.PlanResponseDto;
import com.sparta.neonaduriback.post.dto.PostListDto;
import com.sparta.neonaduriback.review.dto.ReviewListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Component
public class Paging {

    // 내가 찜한 여행 조회
    public Page<?> overPages(List<?> postList, int start, int end, Pageable pageable, int pageno) {
        Page<?> pages = new PageImpl<>(postList.subList(start, end), pageable, postList.size());
        if (pageno > pages.getTotalPages()) {
            throw new IllegalArgumentException("요청할 수 없는 페이지 입니다.");
        } else {
            return pages;
        }
    }

    // 인기 게시물 조회, 지역별 조회, 테마별 조회, 검색 결과 조회  페이징
//    public Page<?> overPagesCheck(List<?> postList, int start, int end, Pageable pageable, int pageno) {
//        Page<?> pages = new PageImpl<>(postList.subList(start, end), pageable, (long)postList.size());
//        if (pageno > pages.getTotalPages()) {
//            throw new IllegalArgumentException("요청할 수 없는 페이지 입니다.");
//        } else {
//            return pages;
//        }
//    }

    // 내가 작성한 플랜조회
//    public Page<PostListDto> overPages2(List<PostListDto> postList, int start, int end, Pageable pageable, int pageno) {
//        Page<PostListDto> pages = new PageImpl(postList.subList(start, end), pageable, postList.size());
//        if (pageno > pages.getTotalPages()) {
//            throw new IllegalArgumentException("요청할 수 없는 페이지 입니다.");
//        } else {
//            return pages;
//        }
//    }

    // 후기 조회
//    public Page<ReviewListDto> overPages3(List<ReviewListDto> reviewList, int start, int end, Pageable pageable, int pageno) {
//        Page<ReviewListDto> pages = new PageImpl<>(reviewList.subList(start, end), pageable, (long)reviewList.size());
//        if (pageno > pages.getTotalPages()) {
//            throw new IllegalArgumentException("요청할 수 없는 페이지입니다.");
//        } else {
//            return pages;
//        }
//    }

}
