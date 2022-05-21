package com.sparta.neonaduriback.like.repository;

import com.sparta.neonaduriback.like.model.Likes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Likes, Long> {

    Optional<Likes> findByPostIdAndUserId(Long postId, Long userId);

    Optional<Likes> findByPostId(Long postId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    //게시물 삭제시 해당 게시물 찜한 유저에게서도 삭제돼야함
    void deleteAllByPostId(Long postId);

    Long countByPostId(Long postId);

    List<Likes> findAllByUserIdOrderByModifiedAtDesc(Long userId);
}
