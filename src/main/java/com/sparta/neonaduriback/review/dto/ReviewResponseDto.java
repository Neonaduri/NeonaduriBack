package com.sparta.neonaduriback.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@NoArgsConstructor
@Getter
public class ReviewResponseDto {
    private List<ReviewListDto> reviewList;
    private int totalPage;
    private int totalElements;
    boolean islastPage;

    public ReviewResponseDto(Page<ReviewListDto> reviewList, boolean islastPage) {
        this.reviewList = reviewList.getContent();
        this.totalPage = reviewList.getTotalPages();
        this.totalElements = (int)reviewList.getTotalElements();
        this.islastPage = islastPage;
    }
}
