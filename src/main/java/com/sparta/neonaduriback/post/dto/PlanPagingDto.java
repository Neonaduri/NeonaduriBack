package com.sparta.neonaduriback.post.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PlanPagingDto {

    private List<?> planList=new ArrayList<>();
    private int totalPage;
    private boolean islastPage;

    public PlanPagingDto(Page<?> postDtoList) {

        this.planList= postDtoList.getContent();
        this.totalPage= postDtoList.getTotalPages();
        this.islastPage=postDtoList.isLast();
    }
}
