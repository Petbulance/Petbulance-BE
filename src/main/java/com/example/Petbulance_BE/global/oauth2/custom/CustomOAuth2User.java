package com.example.Petbulance_BE.global.oauth2.custom;

import com.example.Petbulance_BE.global.oauth2.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final UserDto userDto;

    public CustomOAuth2User(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return userDto.getRole();
            }
        });
        return authorities;
    }

    @Override
    public String getName() {
        return userDto.getEmail();
    }

    public String getProvider(){
        return userDto.getProvider();
    }

    public String getUserId(){
        return userDto.getUserId();
    }

    public LocalDate getBirth(){
        return userDto.getBirth();
    }
}
