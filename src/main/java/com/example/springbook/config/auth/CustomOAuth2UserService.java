package com.example.springbook.config.auth;

import com.example.springbook.config.auth.dto.OAuthAttributes;
import com.example.springbook.config.auth.dto.SessionUser;
import com.example.springbook.domain.user.User;
import com.example.springbook.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    // 기존에 있는 데이터인지 비교하기 위한 userRepository
    private final UserRepository userRepository;
    // 세션 정보를 사용해야 하기 때문에 HttpSession 사용
    private final HttpSession httpSession;

    // 유저를 불러오면 판단해야하기 때문에 loadUser를 재정의해야한다.

    /**
     * 서드 파티 접근을 위한 access Token까지 얻은 다음 실행
     * 서드 파티 : 제 3자, 구글인지 네이버인지
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // DefaultOAuth를 통해 RestOperations로 UserInfo 엔드포인트에 사용자 속성을 요청해서 사용자 정보를 가져와야 하기 때문에
        // CustomOAuth2UserService.loadUser의 동작을 대신 해주는 대리자를 만들었다.
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();

        // 1. "access token을 이용해 서드 파티 서버로부터 User 정보를 받아온다.
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 서드 파티를 구분하기 위한 아이디("naver","google"등)
        String registrationId = userRequest
                .getClientRegistration().getRegistrationId();// 현재 로그인 진행 중인 서비스를 구분한 코드, 네이버로그인인지, 구글로그인인지 구분하는 용도

       // 로그인
        // 로그인 진행 시 키가 되는 필드 값(primary key와 같은 의미)
        String userNameAttributeName = userRequest
                .getClientRegistration()    // 클라이언트
                .getProviderDetails()       // 공급자 상세 : 결국 클라이언트가 누구인지.
                .getUserInfoEndpoint()      // 클라이언트에서 유저정보에 대한 내용
                .getUserNameAttributeName();
                                            // 구글은 기본적으로 코드를 지원, 네이버나 카카오 등은 지원하지 않는다.
                                            // 이후 네이버 로그인과 구글 로그인을 동시 지원할 떄 사용한다.

        //OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스
        OAuthAttributes attributes = OAuthAttributes
                .of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 로그인 한 유저 정보
        User user = saveOrUpdate(attributes);

        // 세션에 사용자 정보를 저장하기 위한 Dto 클래스
        httpSession.setAttribute("user", new SessionUser(user));

        // 로그인한 유저를 리턴함(DefaultOAuth2User)
        return new DefaultOAuth2User(
                // 단 한개의 객체만 저장 가능한 컬렉션을 만들고 싶을 때 사용한다.
                Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()))
                , attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    // OAuth2에서 넘어온 데이터가 이미 저장하고 있는 데이터면 업데이트
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
