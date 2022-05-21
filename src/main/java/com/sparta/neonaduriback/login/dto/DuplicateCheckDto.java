package com.sparta.neonaduriback.login.dto;

/**
 * [dto] - [user] 아이디 중복검사 DuplicateCheckDto
 *
 * @class   : DuplicateCheckDto
 * @author  : 오예령
 * @since   : 2022.05.03
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 */


import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DuplicateCheckDto {
    private String userName;
}
