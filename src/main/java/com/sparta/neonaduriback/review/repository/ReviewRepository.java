package com.sparta.neonaduriback.review.repository;

import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.review.dto.ReviewWithdrawalDto;
import com.sparta.neonaduriback.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // postId로 해당 게시물의 후기 list 가져오기
    List<Review> findAllByPostIdOrderByCreatedAtDesc(Long postId);
    //게시물의 총 리뷰개수 구하기
    Long countByPostId(Long postId);
    //게시물 지워질때 해당 리뷰도 지워짐
    void deleteAllByPostId(Long postId);
    //내가 쓴 후기 조회
    List<Review> findAllByUserOrderByCreatedAtDesc(User user);

    // 탈퇴한 유저의 후기 찾아오기
    List<Review> findAllByUser(User user);

    // 탈퇴한 유저의 후기 삭제
    void deleteAllByUser(User user);
}
