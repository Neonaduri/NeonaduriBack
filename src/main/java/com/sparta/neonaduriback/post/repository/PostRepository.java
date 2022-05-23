package com.sparta.neonaduriback.post.repository;

import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.post.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    //내가 찜한 여행 목록 조회
    List<Post> findAllByUserOrderByCreatedAtDesc(User user);

    //찜한 개수 순으로 조회(ispublic=true) 중에!
    List<Post> findAllByIspublicTrueOrderByLikeCntDesc();

    //방 생선한 사람이 저장하는게 아니면 예외처리
    Optional<Post> findByUserAndPostUUID(User user, String postUUID);

    //지역별 검색
    List<Post> findAllByLocationOrderByLikeCntDesc(String location);

    //지역별 검색
    List<Post> findAllByThemeOrderByLikeCntDesc(String theme);

    //검색 결과
    @Query("Select p from Post p where p.postTitle like %:postTitle% or p.location like %:location% or p.theme like %:theme% order by p.createdAt desc")
    List<Post> findByPostTitleContainingOrLocationContainingOrThemeContainingOrderByCreatedAtDesc(String postTitle, String location, String theme);

    Optional<Post> findByPostUUID(String postUUID);

    void deleteByPostUUID(String postUUID);

//    Optional<Post> findByIdAndPlanTimeDesc(Long postId, int planTime);

//    List<PostDetail> findByPlaceIdOrderByPlanTimeAsc(Long placeId);

//    @Query(nativeQuery = true, value = "SELECT a.*, b.*, c.*" +
//            "FROM places a" +
//            "         LEFT JOIN days b on a.places = b.day_id" +
//            "         LEFT JOIN post c on b.days = c.post_id WHERE c.post_id =:postId order by a.plan_time ASC")
//    Optional<Post> test(@Param("postId") Long postId);

}
