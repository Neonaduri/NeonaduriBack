package com.sparta.neonaduriback.post.days.repository;


import com.sparta.neonaduriback.post.days.model.Days;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DaysRepository extends JpaRepository<Days, Long> {

//    List<Days> findByPlacesIn(Collection<List<Places>> places);
}
