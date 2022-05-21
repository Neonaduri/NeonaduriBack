package com.sparta.neonaduriback.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    private String reviewContents;
    private String reviewImgUrl;

    public ReviewRequestDto(String reviewContents, String reviewImgUrl) {
        this.reviewContents = reviewContents;
        this.reviewImgUrl = reviewImgUrl;
    }
}
