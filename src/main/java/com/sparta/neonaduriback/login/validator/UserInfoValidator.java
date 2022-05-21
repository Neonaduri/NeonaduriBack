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


import com.sparta.neonaduriback.like.repository.LikeRepository;
import com.sparta.neonaduriback.login.dto.IsLoginDto;
import com.sparta.neonaduriback.login.dto.SignupRequestDto;
import com.sparta.neonaduriback.login.repository.UserRepository;
import com.sparta.neonaduriback.post.model.Post;
import com.sparta.neonaduriback.post.repository.PostRepository;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RestControllerAdvice
@Component
public class UserInfoValidator {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;

    @Autowired
    public UserInfoValidator(UserRepository userRepository, PostRepository postRepository,
                             LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
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
    public boolean idDuplichk(String userName){
        return userRepository.findByUserName(userName).isPresent();
    }

    //로그인 확인
    public IsLoginDto isloginCheck(UserDetailsImpl userDetails){


        System.out.println(userDetails.getUser().getUserName());
        String userName = userDetails.getUsername();
        String nickName = userDetails.getNickName();
        String profileImgUrl = userDetails.getProfileImgUrl();

//        Optional<User> user = userRepository.findByUserName(userName);
        return IsLoginDto.builder()
                .userName(userName)
                .nickName(nickName)
                .profileImgUrl(profileImgUrl)
                .build();
    }

    public int getTotalLike(UserDetailsImpl userDetails) {

        //내가 쓴 게시물 다 조회
        List<Post> posts=postRepository.findAllByUserOrderByModifiedAtDesc(userDetails.getUser());
        int totalLike=0;

        //내가 쓴 게시물이 있다면 찜 엔티티에서 게시물 갯수 카운트 -> 유저들한테 찜받은 갯수를 말함
        for(Post eachPost: posts){
            Long postId=eachPost.getPostId();
            totalLike+=likeRepository.countByPostId(postId);
        }

        return totalLike;
    }

}