package com.sparta.neonaduriback.common.image.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@NoArgsConstructor
@Getter
@Entity
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = true)
    private Long userId;

    public Image(String filename, String imageUrl, Long userId) {
        this.filename = filename;
        this.imageUrl = imageUrl;
        this.userId = userId;
    }
    //소셜 로그인 가입 시 이미지 repo 등록
    public Image(String profileImgUrl,Long userId) {
        this.filename = profileImgUrl + "1";
        this.imageUrl = profileImgUrl;
        this.userId = userId;
    }
}