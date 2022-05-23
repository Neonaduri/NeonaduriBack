package com.sparta.neonaduriback.post.places;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlacesRepository extends JpaRepository<Places, Long> {

//    Optional<Places> findByPlaceIdOrderByPlanTime(Long placeId);

    // planTime으로 place들을 정렬하고 싶어

@Query(nativeQuery = true, value = "SELECT a.*, b.*, c.*" +
        "FROM places a" +
        "         LEFT JOIN days b on a.days_id = b.day_id" +
        "         LEFT JOIN post c on b.post_id = c.post_id WHERE c.post_id =:postId order by a.plan_time ASC")

    void test(@Param("postId") Long postId);
//    Optional<Places> findOrderByPlanTime(@Param("postId") Long postId);

//    List<PostDetail> findByPlaceIdOrderByPlanTimeAsc(Long placeId);

//    Optional<Places> findByIdOrderByPlanTimeAsc(Long placeId);



}