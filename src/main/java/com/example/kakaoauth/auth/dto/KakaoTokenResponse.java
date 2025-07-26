package com.example.kakaoauth.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoTokenResponse {

    @JsonProperty("token_type")
    public String tokenType; //bearer로 고정

    @JsonProperty("access_token")
    public String accessToken; //사용자 Access Token 값

    @JsonProperty("id_token")
    public String idToken; //ID 토큰값

    @JsonProperty("expires_in")
    public int expiresIn; //Access Token / ID Token 만료 시간

    @JsonProperty("refresh_token")
    public String refreshToken; //사용자 RefreshToken 값

    @JsonProperty("refresh_token_expires_in")
    public Integer refreshTokenExpiresIn;

    @JsonProperty("scope")
    public String scope;

}
