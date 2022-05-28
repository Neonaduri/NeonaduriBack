package com.sparta.neonaduriback.common.image.repository;

import com.sparta.neonaduriback.common.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByImageUrl(String imageUrl);
    void deleteByFilename(String filename);

    //회원 탈퇴 시 프로필이미지 삭제
    void deleteByUserId(Long userId);
    //회원 탈퇴 전 조회
    Optional<Image> findByUserId(Long userId);

    void deleteByImageUrl(String imageUrl);
}