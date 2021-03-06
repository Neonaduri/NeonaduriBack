package com.sparta.neonaduriback.post.dto;

import com.sparta.neonaduriback.login.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class RoomMakeRequestDto {

    private Long postId;
    private String postUUID;
    private String startDate;
    private String endDate;
    private int dateCnt;
    private String postTitle;
    private String location;
    private String theme;
    private User user;
}
