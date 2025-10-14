package com.example.Petbulance_BE.global.oauth2.custom;

import com.example.Petbulance_BE.domain.user.entity.Users;
import com.example.Petbulance_BE.domain.user.repository.UsersJpaRepository;
import com.example.Petbulance_BE.domain.userEmail.UserEmails;
import com.example.Petbulance_BE.domain.userEmail.repository.UserEmailsJpaRepository;
import com.example.Petbulance_BE.global.common.type.Role;
import com.example.Petbulance_BE.global.oauth2.dto.UserDto;
import com.example.Petbulance_BE.global.oauth2.response.GoogleResponse;
import com.example.Petbulance_BE.global.oauth2.response.KakaoResponse;
import com.example.Petbulance_BE.global.oauth2.response.NaverResponse;
import com.example.Petbulance_BE.global.oauth2.response.OAuth2Response;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserEmailsJpaRepository userEmailsJpaRepository;
    private final UsersJpaRepository usersJpaRepository;

    public CustomOAuth2UserService(UserEmailsJpaRepository userEmailsJpaRepository,UsersJpaRepository usersJpaRepository) {
        this.userEmailsJpaRepository = userEmailsJpaRepository;
        this.usersJpaRepository = usersJpaRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {


        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = null;

        if(registrationId.equals("naver")){
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }else if(registrationId.equals("google")){
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }else if(registrationId.equals("kakao")){
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }else{
            return null;
        }


        String provider = oAuth2Response.getProvider();
        String email = oAuth2Response.getEmail();
        switch (provider) {
            case "google" -> {
                Optional<Users> googleUser = userEmailsJpaRepository.findByGoogleEmail(email);
                if (googleUser.isPresent()) {
                    Users user = googleUser.get();
                    UserDto userdto = new UserDto();
                    userdto.setUserId(user.getId());
                    userdto.setEmail(email);
                    userdto.setProvider(provider);
                    userdto.setRole(user.getRole().name());

                    return new CustomOAuth2User(userdto);
                } else {
                    Users user = Users.builder()
                            .role(Role.ROLE_CLIENT)
                            .googleConnected(true)
                            .build();

                    usersJpaRepository.save(user);

                    UserEmails userEmails = UserEmails.builder()
                            .googleEmail(email)
                            .user(user)
                            .build();
                    userEmailsJpaRepository.save(userEmails);

                    UserDto userdto = new UserDto();
                    userdto.setUserId(user.getId());
                    userdto.setEmail(email);
                    userdto.setProvider(provider);
                    userdto.setRole(user.getRole().name());

                    return new CustomOAuth2User(userdto);
                }
            }
            case "kakao" -> {
                Optional<Users> kakaoUser = userEmailsJpaRepository.findByKakaoEmail(email);
                if (kakaoUser.isPresent()) {
                    Users user = kakaoUser.get();
                    UserDto userdto = new UserDto();
                    userdto.setUserId(user.getId());
                    userdto.setEmail(email);
                    userdto.setProvider(provider);
                    userdto.setRole(user.getRole().name());

                    return new CustomOAuth2User(userdto);
                } else {
                    Users user = Users.builder()
                            .role(Role.ROLE_CLIENT)
                            .kakaoConnected(true)
                            .build();

                    usersJpaRepository.save(user);

                    UserEmails userEmails = UserEmails.builder()
                            .kakaoEmail(email)
                            .user(user)
                            .build();
                    userEmailsJpaRepository.save(userEmails);

                    UserDto userdto = new UserDto();
                    userdto.setUserId(user.getId());
                    userdto.setEmail(email);
                    userdto.setProvider(provider);
                    userdto.setRole(user.getRole().name());

                    return new CustomOAuth2User(userdto);
                }
            }
            case "naver" -> {
                Optional<Users> naverUser = userEmailsJpaRepository.findByNaverEmail(email);
                if (naverUser.isPresent()) {
                    Users user = naverUser.get();
                    UserDto userdto = new UserDto();
                    userdto.setUserId(user.getId());
                    userdto.setEmail(email);
                    userdto.setProvider(provider);
                    userdto.setRole(user.getRole().name());

                    return new CustomOAuth2User(userdto);
                } else {
                    Users user = Users.builder()
                            .role(Role.ROLE_CLIENT)
                            .naverConnected(true)
                            .build();

                    usersJpaRepository.save(user);

                    UserEmails userEmails = UserEmails.builder()
                            .naverEmail(email)
                            .user(user)
                            .build();
                    userEmailsJpaRepository.save(userEmails);

                    UserDto userdto = new UserDto();
                    userdto.setUserId(user.getId());
                    userdto.setEmail(email);
                    userdto.setProvider(provider);
                    userdto.setRole(user.getRole().name());

                    return new CustomOAuth2User(userdto);
                }
            }
        }

        return null;
    }
}
