package com.sparta.neonaduriback.post.places;

import com.sparta.neonaduriback.common.model.Timestamped;
import com.sparta.neonaduriback.post.dto.PlaceRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Places extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = true, length = 500)
    private String placeInfoUrl;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private String placeMemo;

    @Column(nullable = true)
    private int planTime;

    @Column(nullable = false)
    private String lat;

    @Column(nullable = false)
    private String lng;

//    @ManyToOne
//    @JoinColumn(name = "DAYS_ID")
//    private Days days;

    public Places(PlaceRequestDto placeRequestDto){
        this.placeName=placeRequestDto.getPlaceName();
        this.placeInfoUrl=placeRequestDto.getPlaceInfoUrl();
        this.category=placeRequestDto.getCategory();
        this.address=placeRequestDto.getAddress();
        this.roadAddress=placeRequestDto.getRoadAddress();
        this.placeMemo=placeRequestDto.getPlaceMemo();
        this.planTime=placeRequestDto.getPlanTime();
        this.lat=placeRequestDto.getLat();
        this.lng=placeRequestDto.getLng();
    }
}
