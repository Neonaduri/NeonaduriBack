package com.sparta.neonaduriback.login.service;

/**
 * [Service] - 구글 소셜 로그인 Service
 *
 * @class   : GoogleLoginService
 * @author  : 오예령
 * @since   : 2022.04.30
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *  2022.05.04 오예령       email 값 userName으로 변경, 유저 정보 조회 항목 변경
 *                         유저 정보 조회 Url 변경
 *  2022.05.06 오예령       JwtProperties 클래스 삭제 및 header에 토큰 넣는 코드 수정
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.neonaduriback.common.image.model.Image;
import com.sparta.neonaduriback.common.image.repository.ImageRepository;
import com.sparta.neonaduriback.login.dto.SocialLoginInfoDto;
import com.sparta.neonaduriback.login.model.User;
import com.sparta.neonaduriback.login.repository.UserRepository;
import com.sparta.neonaduriback.security.UserDetailsImpl;
import com.sparta.neonaduriback.security.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleLoginService {

//    @Value("${google.client-id}")
//    String googleClientId;
//
//    @Value("${google.client-secret}")
//    String googleClientSecret;

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    // 구글 로그인
    public SocialLoginInfoDto googleLogin(String code, HttpServletResponse response) throws JsonProcessingException {

        // 1. 인가코드로 엑세스토큰 가져오기
        String accessToken = getAccessToken(code);

        // 2. 엑세스토큰으로 유저정보 가져오기
        SocialLoginInfoDto googleUserInfo = getGoogleUserInfo(accessToken);

        // 3. 유저확인 & 회원가입
        User foundUser = getUser(googleUserInfo);

        // 4. 시큐리티 강제 로그인
        Authentication authentication = securityLogin(foundUser);

        // 5. jwt 토큰 발급
        jwtToken(response, authentication);
        return googleUserInfo;
    }

    // 1. 인가코드로 엑세스토큰 가져오기
    private String getAccessToken(String code) throws JsonProcessingException {

        // 헤더에 Content-type 지정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 바디에 필요한 정보 담기
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("client_id" , "68742741278-1598oqkkoch3q3g0oaudc2lahovbsc64.apps.googleusercontent.com"); // 리액트
        body.add("client_secret", "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");  // 리액트
        body.add("code", code);

//        body.add("redirect_uri", "http://localhost:3000/user/google/callback"); // 리액트 (local)
        body.add("redirect_uri", "https://neonaduri.com/user/google/callback"); // 리액트 (서버 배포 후)
        body.add("grant_type", "authorization_code");

        // POST 요청 보내기
        HttpEntity<MultiValueMap<String, String>> googleToken = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST, googleToken,
                String.class
        );

        // response에서 엑세스토큰 가져오기
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseToken = objectMapper.readTree(responseBody);
        return responseToken.get("access_token").asText();
    }

    // 2. 엑세스토큰으로 유저정보 가져오기
    private SocialLoginInfoDto getGoogleUserInfo(String accessToken) throws JsonProcessingException {

     RestTemplate restTemplate = new RestTemplate();

        ObjectMapper mapper = new ObjectMapper();

        String requestUrl = UriComponentsBuilder.fromHttpUrl("https://openidconnect.googleapis.com/v1/userinfo")
                .queryParam("access_token", accessToken).encode().toUriString();

        String resultJson = restTemplate.getForObject(requestUrl, String.class);

        Map<String,String> googleUserInfo = mapper.readValue(resultJson, new TypeReference<Map<String, String>>(){});

        String userName = googleUserInfo.get("email");
        String nickName = googleUserInfo.get("name");
        String profileImgUrl = googleUserInfo.get("picture");

        return new SocialLoginInfoDto(userName,nickName,profileImgUrl);

    }

    // 3. 유저확인 & 회원가입
    private User getUser(SocialLoginInfoDto googleUserInfo) {

        String userName = googleUserInfo.getUserName();
        String nickName = googleUserInfo.getNickName();

        String password = passwordEncoder.encode(UUID.randomUUID().toString());
        String profileImgUrl = googleUserInfo.getProfileImgUrl();

        // DB에서 userName으로 가져오기 없으면 회원가입
        User googoleUser = userRepository.findByUserName(userName).orElse(null);

        if (googoleUser == null) {
            googoleUser = User.builder()
                    .userName(userName)
                    .nickName(nickName)
                    .password(password)
                    .profileImgUrl(profileImgUrl)
                    .build();
            userRepository.save(googoleUser);

            Image image = new Image(profileImgUrl, googoleUser.getId());
            imageRepository.save(image);
        }
        return googoleUser;
    }

    // 4. 시큐리티 강제 로그인
    private Authentication securityLogin(User findUser) {

        // userDetails 생성
        UserDetailsImpl userDetails = new UserDetailsImpl(findUser);
        log.warn("google 로그인 완료 : " + userDetails.getUser().getUserName());
        // UsernamePasswordAuthenticationToken 발급
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        // 강제로 시큐리티 세션에 접근하여 authentication 객체를 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

    // 5. jwt 토큰 발급
    private void jwtToken(HttpServletResponse response, Authentication authentication) {

        UserDetailsImpl userDetailsImpl = ((UserDetailsImpl) authentication.getPrincipal());
        String token = JwtTokenUtils.generateJwtToken(userDetailsImpl);
        response.addHeader("Authorization", "BEARER" + " " + token);

    }
}