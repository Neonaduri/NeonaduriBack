package com.sparta.neonaduriback.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class ReviewListDto {
    private Long reviewId;
    private String nickName;
    private String profileImgUrl;
    private String reviewContents;
    private String reviewImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ReviewListDto(Long id, String nickName, String profileImgUrl, String reviewContents, String reviewImgUrl, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.reviewId = id;
        this.nickName = nickName;
        this.profileImgUrl = profileImgUrl;
        this.reviewContents = reviewContents;
        this.reviewImgUrl = reviewImgUrl;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }
}
