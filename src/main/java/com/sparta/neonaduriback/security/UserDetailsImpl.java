package com.sparta.neonaduriback.security;

/**
 * [security] - [provider] UserDetailsImpl
 *
 * @class   : UserDetailsImpl
 * @author  : 오예령
 * @since   : 2022.05.03
 * @version : 1.0
 *
 *   수정일     수정자             수정내용
 *  --------   --------    ---------------------------
 *  2022.05.03 오예령       회원 정보에 프로필이미지(profileImgUrl)와 받은 좋아요 개수(totalLike) 추가
 *  2022.05.07 오예령       토큰 안에 totalLike 제거
 */


import com.sparta.neonaduriback.login.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

//    public Long userId;
//    public String nickName;


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    public String getNickName(){
        return user.getNickName();
    }

    public String getProfileImgUrl(){
        return user.getProfileImgUrl();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return null;
    }
}