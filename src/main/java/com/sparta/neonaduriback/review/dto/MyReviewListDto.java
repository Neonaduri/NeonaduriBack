package com.sparta.neonaduriback.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MyReviewListDto {

    private Long reviewId;
    private Long postId;
    private String nickName;
    private String reviewContents;
    private String reviewImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
