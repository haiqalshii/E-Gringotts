package com.bankhaven.egringotts.dto.request.account;

import lombok.Data;

@Data
public class SearchRequestDto {
    private String userInfo;

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}