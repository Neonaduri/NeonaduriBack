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

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IsLoginDto {
    private String userName;
    private String nickName;
    private String profileImgUrl;

}
