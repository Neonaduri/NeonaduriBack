package com.sparta.neonaduriback.post.dto;

import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.places.Places;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DaysDto {
    private Long dayId;
    private int dateNumber;
//    private Post post;
    private List<Places> places = new ArrayList<>();

//    public DaysDto(Long dayId, int dateNumber, List<Places> places) {
//        this.dayId = dayId;
//        this.dateNumber = dateNumber;
//        this.places = places;
//    }
//
//    public DaysDto(Long dayId, int dateNumber, List<PlacesDto> placesDto) {
//        this.dayId = dayId;
//        this.dateNumber = dateNumber;
//        this.places = placesDto;
//    }
}