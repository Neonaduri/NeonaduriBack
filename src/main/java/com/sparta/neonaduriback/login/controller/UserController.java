package com.sparta.neonaduriback.login.controller;

/**
 * [controller] - userController
 *
 * @class   : userController
 * @author  : 오예령
 * @since   : 2022.04.30
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.neonaduriback.login.dto.*;
import com.sparta.neonaduriback.login.service.GoogleLoginService;
import com.sparta.neonaduriback.login.service.KakaoUserService;
import com.sparta.neonaduriback.login.service.UserService;
import com.sparta.neonaduriback.login.validator.UserInfoValidator;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class UserController {
    private final KakaoUserService kakaoUserService;
    private final GoogleLoginService googleLoginService;
    private final UserService userService;
    private final UserInfoValidator userInfoValidator;

    // 회원가입
    @PostMapping("/user/signup")
    public ResponseEntity<String> register(@RequestBody SignupRequestDto signupRequestDto, @Valid BindingResult bindingResult) {
        return userService.registerUser(signupRequestDto, bindingResult);
    }

    // 카카오 로그인
    @GetMapping("/user/kakao/callback")
    public SocialLoginInfoDto kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        return kakaoUserService.kakaoLogin(code, response);
    }

    // 구글 로그인
    @GetMapping("/user/google/callback")
    public SocialLoginInfoDto googleLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
       return googleLoginService.googleLogin(code, response);
    }

     // 아이디 중복검사
    @PostMapping("/idcheck")
    public ResponseEntity<String> idcheck(@RequestBody DuplicateCheckDto duplicateCheckDto) {
        return userInfoValidator.idDuplichk(duplicateCheckDto.getUserName());
    }

    // 유저 정보 확인
    @GetMapping("/islogin")
    private ResponseEntity<IsLoginDto> isloginChk(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userInfoValidator.isloginCheck(userDetails);
    }

    // 유저프로필 수정
    @PutMapping("/user/mypage")
    public ResponseEntity<String> updateUserInfo(@RequestParam(value = "profileImgFile") MultipartFile multipartFile,
                                                        @RequestParam String profileImgUrl,
                                                        @RequestParam("nickName") String nickName,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        // 닉네임 값이 비었을 경우 기존 닉네임 값을 지정해줌
        if (nickName.equals("")) {
            nickName = userDetails.getNickName();
        }
        Long userId = userDetails.getUser().getId();
        //파일이 비었다는 것은 사용자가 이미지를 삭제했다거나 , 사진 수정하지 않았다는 것
        if (multipartFile.isEmpty()){

            userService.deleteProfileImg(profileImgUrl,nickName,userId);
        } else {
            //사용자가 이미지를 수정함
            userService.updateUserInfo(multipartFile, nickName, userId);

        }
        return ResponseEntity.status(201).body("201");
    }

// 비밀번호 변경
    @PutMapping("/updatePassword")
    public ResponseEntity<Boolean> updatePassword(@RequestBody PasswordRequestDto passwordRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.updatePassword(passwordRequestDto, userDetails);
    }

// 회원 탈퇴
    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetails) throws Exception {
        return userService.withdrawal(userDetails);
    }
}
