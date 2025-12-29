package org.example.identityservice.dto;


public record AuthResult(
        String jwtCookie,
        UserInfoResponse userInfo
) {
}
