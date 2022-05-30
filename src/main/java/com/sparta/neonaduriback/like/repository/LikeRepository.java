package com.sparta.neonaduriback.like.repository;

import com.sparta.neonaduriback.like.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByPostIdAndUserId(Long postId, Long userId);
    //스크랩 취소
    void deleteByPostIdAndUserId(Long postId, Long userId);
    //게시물 삭제시 해당 게시물 스크랩한 유저에게서도 삭제돼야함
    void deleteAllByPostId(Long postId);
    //게시물이 스크랩 몇번 됐는지 판단
    Long countByPostId(Long postId);

    List<Likes> findAllByUserIdOrderByModifiedAtDesc(Long userId);
}
