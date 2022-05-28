package com.sparta.neonaduriback.review.dto;

import com.sparta.neonaduriback.login.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long reveiwId;
    private String reviewContents;
    private String reviewImgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private User user;

    // 후기 내용만 등록
    public ReviewResponseDto(Long id, String reviewContetns, LocalDateTime createdAt, LocalDateTime modifiedAt, User user) {
        this.reveiwId = id;
        this.reviewContents = reviewContetns;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.user = user;
    }
}
