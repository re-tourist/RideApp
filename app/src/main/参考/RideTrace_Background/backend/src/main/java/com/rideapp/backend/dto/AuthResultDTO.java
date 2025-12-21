package com.rideapp.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResultDTO {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("username")
    private String username;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("token")
    private String token;

    public AuthResultDTO() {
    }

    public AuthResultDTO(Long userId, String username, String nickname, String email, String avatarUrl, String bio, String gender, String token) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.gender = gender;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
