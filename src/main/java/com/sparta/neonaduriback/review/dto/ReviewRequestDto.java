package com.sparta.neonaduriback.review.dto;

import com.sparta.neonaduriback.login.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.jni.Local;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ReviewRequestDto {

    private String reviewContents;
    private String reviewImgUrl;
    private User user;

    // 후기 내용만 등록
    public ReviewRequestDto(String reviewContetns, User user) {
        this.reviewContents = reviewContetns;
        this.user = user;
    }

    public ReviewRequestDto(String reviewContents, String reviewImgUrl, User user) {
        this.reviewContents = reviewContents;
        this.reviewImgUrl = reviewImgUrl;
        this.user = User.builder().build();
    }

}
