package com.sparta.neonaduriback.login.model;

/**
 * [model] - User
 *
 * @class   : User
 * @author  : 오예령
 * @since   : 2022.04.30
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *  2022.05.03 오예령       회원 정보에 profileImgUrl, totalLike 추가
 *  2022.05.07 오예령       카카오, 구글 소셜 로그인 수정
 */

import com.sparta.neonaduriback.login.dto.SignupRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = true, length = 500)
    private String profileImgUrl;

//     비밀번호 변경
    public void updateUserPassword(String password) {
        this.password=password;
}

    // 회원가입
    public User(String userName, String password, SignupRequestDto signupRequestDto) {
        this.userName = userName;
        this.password = password;
        this.nickName = signupRequestDto.getNickName();
        this.profileImgUrl = "https://seunghodev-bucket.s3.ap-northeast-2.amazonaws.com/default/Group+1.png";
    }

    // 카카오 회원가입 + 구글 회원가입
    @Builder
    public User(String userName, String nickName, String password, String profileImgUrl) {
        this.userName = userName;
        this.nickName = nickName;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
    }

    //회원 프로필 업데이트
    public void update(String profileImgUrl, String nickName){
        this.profileImgUrl=profileImgUrl;
        this.nickName=nickName;
    }
    //회원 프로필이미지가 repository에  없어도 닉네임만 변경가능하게끔 하기 위함
    public void update(String nickName){
        this.nickName=nickName;
    }

}