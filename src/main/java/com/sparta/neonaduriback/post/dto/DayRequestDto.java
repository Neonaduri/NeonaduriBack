package com.sparta.neonaduriback.post.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class DayRequestDto {

//    private int dateNumber;
    //day 일자는 따로 받기, 저장 시 for문 인덱스값으로 일자
    private List<PlaceRequestDto> places=new ArrayList<>();
}
