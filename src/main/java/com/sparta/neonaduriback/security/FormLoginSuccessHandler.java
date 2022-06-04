package com.sparta.neonaduriback.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.neonaduriback.security.jwt.JwtTokenUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

// 3번 - 로그인 성공하면
public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());

        // 4번 호출 - JWTUtils 로 이동!
        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);

        // 5번이 되면서 끝난다.
        response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String,String > hashMap = new HashMap<>();
        hashMap.put("userName",userDetails.getUsername());
        String msg = new String (objectMapper.writeValueAsString(hashMap).getBytes("UTF-8"), "ISO-8859-1");
        response.getOutputStream()
                .println(msg);

    }

}
