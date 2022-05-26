package com.sparta.neonaduriback.login.dto;

/**
 * [dto] - [user] 로그인 회원 정보 조회 IsLoginDto
 *
 * @class   : IsLoginDto
 * @author  : 오예령
 * @since   : 2022.05.03
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 */

import com.sparta.neonaduriback.login.model.User;
import lombok.*;

@Getter
@Setter
public class IsLoginDto {
    private String userName;
    private String nickName;
    private String profileImgUrl;

    public IsLoginDto(User user) {
        this.userName = user.getUserName();
        this.nickName = user.getNickName();
        this.profileImgUrl = user.getProfileImgUrl();
    }
}
