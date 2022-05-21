package com.sparta.neonaduriback.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PlaceRequestDto {

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
