package com.example.kakaoauth.auth.controller;

import com.example.kakaoauth.auth.dto.KakaoUserInfoResponse;
import com.example.kakaoauth.auth.service.KakaoService;
import com.example.kakaoauth.global.response.ApiResponse;
import com.example.kakaoauth.user.dto.LoginResponseDto;
import com.example.kakaoauth.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = "application/json; charset=UTF-8")
public class KakaoAuthController {

    private final KakaoService kakaoService;
    private final UserService userService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    /**
     * 프론트에서 카카오 로그인 버튼 누르면 리다이렉트되는 엔드포인트
     */
    @GetMapping("/login")
    public ResponseEntity<Void> redirectToKakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(kakaoAuthUrl));

        return ResponseEntity.status(302).headers(headers).build();
    }

    /**
     * 카카오에서 리다이렉트된 후 호출되는 콜백 엔드포인트
     */
    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<LoginResponseDto>> callback(@RequestParam("code") String code, HttpSession session) {
        try {
            String accessToken = kakaoService.getAccessTokenFromKakao(code);
            KakaoUserInfoResponse userInfo = kakaoService.getUserInfo(accessToken);
            LoginResponseDto loginResponseDto = userService.loginWithKakao(userInfo);

            // 세션에 저장
            session.setAttribute("kakaoId", userInfo.getId());
            session.setAttribute("kakaoAccessToken", accessToken);

            String message = loginResponseDto.isNewUser()
                    ? "신규 유저입니다."
                    : "로그인이 완료되었습니다.";
            HttpStatus status = loginResponseDto.isNewUser()
                    ? HttpStatus.CREATED
                    : HttpStatus.OK;

            return ResponseEntity.status(status)
                    .body(ApiResponse.of(status.value(), message, loginResponseDto));
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 예외 발생", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.of(HttpStatus.UNAUTHORIZED.value(), "카카오 로그인 실패", null));
        }
    }
}
