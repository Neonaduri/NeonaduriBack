package com.sparta.neonaduriback.login.validator;

/**
 * [validator] - 유효성 검사
 *
 * @class   : UserInfoValidator
 * @author  : 오예령
 * @since   : 2022.04.30
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *  2022.05.06 오예령       유효성 검사 추가, 아이디 중복 체크
 *  2022.05.07 오예령       유저 정보 조회 return 형태 변경 (리팩토링)
 */


import com.sparta.neonaduriback.login.dto.IsLoginDto;
import com.sparta.neonaduriback.login.dto.SignupRequestDto;
import com.sparta.neonaduriback.login.repository.UserRepository;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestControllerAdvice
@Component
public class UserInfoValidator {

    private final UserRepository userRepository;

    @Autowired
    public UserInfoValidator(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public String getValidMessage(SignupRequestDto signupRequestDto, Errors errors) {

        String regex = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        String str = signupRequestDto.getUserName();

        if (errors.hasErrors()) {
            Map<String, String> validatorResult = validateHandling(errors);
            return validatorResult.get("message");
        } else if (!Pattern.matches(regex,str)) {
            return "올바른 이메일 형식이 아닙니다.";
        } else if (userRepository.findByUserName(signupRequestDto.getUserName()).isPresent()) {
            return "중복된 아이디가 존재합니다.";
        } else if (signupRequestDto.getPassword().length() < 4) {
            return "비밀번호는 4자리 이상 12자리 미만입니다.";
        } else if (!signupRequestDto.getPassword().equals(signupRequestDto.getPasswordCheck())) {
            return "비밀번호가 일치하지 않습니다.";
        } else {
            return "회원가입 성공";
        }
    }

    // 회원가입 시, 유효성 체크
    public Map<String, String> validateHandling(Errors errors) {
        Map<String, String> validatorResult = new HashMap<>();

        for (FieldError error : errors.getFieldErrors()) {
            String validKeyName = "message";
            validatorResult.put(validKeyName, error.getDefaultMessage());
        }
        return validatorResult;
    }

    // 아이디 중복체크
    public ResponseEntity<String> idDuplichk(String userName){
        if (!userRepository.findByUserName(userName).isPresent()) {
            return ResponseEntity.status(201).body("201");
        } return ResponseEntity.status(400).body("400");
    }

    //로그인 확인
    public ResponseEntity<IsLoginDto> isloginCheck(UserDetailsImpl userDetails){
        System.out.println(userDetails.getUser().getUserName());
        IsLoginDto isLoginDto = new IsLoginDto(userDetails.getUser());
        return ResponseEntity.status(200).body(isLoginDto);
    }

}