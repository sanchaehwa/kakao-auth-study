package com.example.kakaoauth.auth.service;

import com.example.kakaoauth.auth.dto.KakaoTokenResponse;
import com.example.kakaoauth.auth.dto.KakaoUserInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final WebClient kakaoAuthWebClient;
    private final WebClient kakaoApiWebClient;

    @Value("${kakao.client_id}")
    private String kakaoClientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    // access token 받기
    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponse kakaoTokenResponse = kakaoAuthWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoClientId)
                        .queryParam("redirect_uri", redirectUri)
                        .queryParam("code", code)
                        .build(true))
                .retrieve()
                .bodyToMono(KakaoTokenResponse.class)
                .block();

        log.info("[KakaoService] access_token = {}", kakaoTokenResponse.getAccessToken());
        log.info("[KakaoService] refresh_token = {}", kakaoTokenResponse.getRefreshToken());
        log.info("[KakaoService] scope = {}", kakaoTokenResponse.getScope());

        return Objects.requireNonNull(kakaoTokenResponse).getAccessToken();
    }

    // 사용자 정보 조회
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        KakaoUserInfoResponse userInfo = kakaoApiWebClient.get()
                .uri("/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(KakaoUserInfoResponse.class)
                .block();

        log.info("[KakaoService] Auth ID = {}", userInfo.getId());
        log.info("[KakaoService] NickName = {}", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[KakaoService] ProfileImageUrl = {}", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    // 로그아웃
    public void kakaoDisconnect(String accessToken) {
        String response = kakaoApiWebClient.post()
                .uri("/v1/user/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("[KakaoService] logout response = {}", response);
    }

    // 회원 탈퇴
    public void unlinkKakaoAccount(String accessToken) {
        kakaoApiWebClient.post()
                .uri("/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("[KakaoService] unlink response = {}", response))
                .doOnError(e -> {
                    throw new RuntimeException("카카오 계정 연결 실패", e);
                })
                .block();
    }
}
