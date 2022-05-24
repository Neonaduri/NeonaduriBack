package com.sparta.neonaduriback.post.places;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacesRepository extends JpaRepository<Places, Long> {

    List<Places> findAllByPlaceIdOrderByPlanTimeAsc(Long placeId);
//    List<Places> findByPlaceNameContaining(String placeName);
}
