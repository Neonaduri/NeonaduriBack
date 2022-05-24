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
 *  2022.05.03 오예령       아이디 중복검사 및 로그인 정보 조회 기능 추가
 *  2022.05.07 오예령       아이디 중복검사 로직 변경
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.neonaduriback.login.dto.*;
import com.sparta.neonaduriback.login.service.GoogleLoginService;
import com.sparta.neonaduriback.login.service.KakaoUserService;
import com.sparta.neonaduriback.login.service.UserService;
import com.sparta.neonaduriback.login.validator.UserInfoValidator;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
    public ResponseEntity<String> registerUser(@RequestBody SignupRequestDto signupRequestDto, Errors errors) {
        String message = userService.registerUser(signupRequestDto, errors);
        if (message.equals("회원가입 성공")) {
            return ResponseEntity.status(201)
                    .body(message);
        } else {
            return ResponseEntity.status(400)
                    .body(message);
        }
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
        if (!userInfoValidator.idDuplichk(duplicateCheckDto.getUserName())) {
             return ResponseEntity.status(201)
                     .body("201");
        } else {
            return ResponseEntity.status(400)
                    .body("400");
        }
    }

    // 유저 정보 확인
    @GetMapping("/islogin")
    private ResponseEntity<IsLoginDto> isloginChk(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        userInfoValidator.isloginCheck(userDetails);
        return new ResponseEntity<>(userInfoValidator.isloginCheck(userDetails), HttpStatus.OK);
    }

    // 유저프로필 수정
    @PutMapping("/user/mypage")
    public ResponseEntity<String> updateUserInfo(@RequestParam(value = "profileImgFile") MultipartFile multipartFile,
                                                        @RequestParam String profileImgUrl,
                                                        @RequestParam("nickName") String nickName,
                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

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
        return ResponseEntity.ok(userService.updatePassword(passwordRequestDto, userDetails));
    }

//    // 회원탈퇴
//    @GetMapping("/withdrawal")
//    public ResponseEntity<Boolean> withdrawal(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return ResponseEntity.ok(userService.withdrawal(userDetails));
//    }
}