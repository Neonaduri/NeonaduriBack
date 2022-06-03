package com.sparta.neonaduriback.post.repository;

import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.service.ShowSearchPostsCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, ShowSearchPostsCustom {

    //내가 찜한 여행 목록 조회
    List<Post> findAllByUserOrderByCreatedAtDesc(User user);

    //찜한 개수 순으로 조회(ispublic=true) 중에!
    List<Post> findAllByIspublicTrueOrderByLikeCntDesc();

    //방 생선한 사람이 저장하는게 아니면 예외처리
    Optional<Post> findByUserAndPostUUID(User user, String postUUID);

    //지역별 검색
    List<Post> findAllByLocationOrderByLikeCntDesc(String location);
    Page<Post> findAllByLocationAndIspublicTrue(String location, Pageable pageable);

    //테마별 검색
    List<Post> findAllByThemeOrderByLikeCntDesc(String theme);
    Page<Post> findAllByThemeAndIspublicTrue(String theme, Pageable pageable);

    //검색 결과
    @Query("Select p from Post p where p.postTitle like %:postTitle% or p.location like %:location% or p.theme like %:theme% order by p.createdAt desc")
    List<Post> findByPostTitleContainingOrLocationContainingOrThemeContainingOrderByCreatedAtDesc(String postTitle, String location, String theme);

    Optional<Post> findByPostUUID(String postUUID);

    void deleteByPostUUID(String postUUID);

    List<Post> findAllByUser(User user);


}
