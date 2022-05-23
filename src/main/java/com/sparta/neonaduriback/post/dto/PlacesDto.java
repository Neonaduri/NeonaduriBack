package com.sparta.neonaduriback.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlacesDto {
    private Long placeId;
    private String placeName;
    private String placeInfoUrl;
    private String category;
    private String address;
    private String roadAddress;
    private String placeMemo;
    private int planTime;
    private String lat;
    private String lng;

}