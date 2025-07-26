package com.example.kakaoauth.user.service;

import com.example.kakaoauth.auth.dto.KakaoUserInfoResponse;
import com.example.kakaoauth.auth.service.KakaoService;
import com.example.kakaoauth.global.ErrorCode;
import com.example.kakaoauth.user.domain.User;
import com.example.kakaoauth.user.dto.LoginResponseDto;
import com.example.kakaoauth.user.exception.NotFoundUserException;
import com.example.kakaoauth.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoService kakaoService;

    public LoginResponseDto loginWithKakao(KakaoUserInfoResponse kakaoUserInfoResponse) {
        Long kakaoId = kakaoUserInfoResponse.getId();
        String nickname = kakaoUserInfoResponse.getKakaoAccount().getProfile().getNickName();
        String profileImageUrl = kakaoUserInfoResponse.getKakaoAccount().getProfile().getProfileImageUrl();

        Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);

        boolean isNewUser = false;

        if (userOptional.isPresent()) {
            User user = userOptional.get();
        } else { //신규 유저
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .profileImage(profileImageUrl)
                    .build();
            userRepository.save(newUser);
            isNewUser = true;
        }

        return new LoginResponseDto(isNewUser, nickname,profileImageUrl);
    }
    //회원 탈퇴
    @Transactional
    public void withdraw(HttpSession session){

        String accessToken = (String) session.getAttribute("accessToken");
        Long kakaoId = getKakaoId(session);

        if (accessToken != null){
            kakaoService.unlinkKakaoAccount(accessToken);
        }
        //DB에서 회원 삭제
        userRepository.deleteByKakaoId(kakaoId);

        session.invalidate();
    }
    //Kakao ID Get
    public long getKakaoId(HttpSession session){
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        return kakaoId;
    }
    //user Get
    public User getUser(Long kakaoId){
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
        return user;
    }
}
