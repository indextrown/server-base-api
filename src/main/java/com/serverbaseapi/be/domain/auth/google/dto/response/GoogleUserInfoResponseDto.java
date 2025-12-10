package com.serverbaseapi.be.domain.auth.google.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class GoogleUserInfoResponseDto {
    private String sub;
    private String email;
    private String name;
    @JsonProperty("picture")
    private String pictureUrl;

    // 생성자
    public GoogleUserInfoResponseDto(String sub,
                                     String email,
                                     String name,
                                     String pictureUrl) {
        this.sub = sub;
        this.email = email;
        this.name = name;
        this.pictureUrl = pictureUrl;
    }
}
