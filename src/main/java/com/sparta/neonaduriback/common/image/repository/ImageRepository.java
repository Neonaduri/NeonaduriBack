package com.sparta.neonaduriback.common.image.repository;
import com.sparta.neonaduriback.common.image.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {


    Optional<Image> findByImageUrl(String imageUrl);
    //S3uploader에서 삭제하려면 filename으로 지워야함
    void deleteByFilename(String filename);
    //회원 탈퇴 시 프로필이미지 삭제
    void deleteByUserId(Long userId);
    //회원 탈퇴 전 조회
    Optional<Image> findByUserId(Long userId);
    //imageRepository에서 '유일한' 사진을 찾음
    Optional<Image> findByImageUrlAndUserId(String imageUrl, Long userId);
    // 후기 삭제 시 imageRepository에서 이미지도 삭제
    void deleteByImageUrl(String imageUrl) ;
}