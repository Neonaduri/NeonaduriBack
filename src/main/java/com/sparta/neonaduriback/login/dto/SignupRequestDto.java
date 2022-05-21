package com.sparta.neonaduriback.login.dto;

/**
 * [dto] - [user] 회원가입 SignupRequestDto
 *
 * @class   : SignupRequestDto
 * @author  : 오예령
 * @since   : 2022.05.03
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 */

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class SignupRequestDto {
    private String userName;
    private String nickName;
    private String password;
    private String passwordCheck;

}
