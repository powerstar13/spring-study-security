package com.sp.fc.web.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sp.fc.user.domain.SpUser;

import java.time.Instant;

public class JWTUtil {

    private static final Algorithm ALGORITHM = Algorithm.HMAC256("master");
    private static final long AUTH_TIME = 2; // 2초
    private static final long REFRESH_TIME = 60 * 60 * 24 * 7; // 일주일

    public static String makeAuthToken(SpUser user) {

        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("exp", Instant.now().getEpochSecond() + AUTH_TIME) // withExpiresAt() 메서드를 쓸 경우 Date를 사용해야하기에 withClaim() 메서드를 이용하여 EpochSecond를 사용해본다.
            .sign(ALGORITHM);
    }

    public static String makeRefreshToken(SpUser user) {

        return JWT.create()
            .withSubject(user.getUsername())
            .withClaim("exp", Instant.now().getEpochSecond() + REFRESH_TIME)
            .sign(ALGORITHM);
    }

    public static VerifyResult verify(String token) {

        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build()
                .verify(token);

            return VerifyResult.builder()
                .success(true)
                .username(verify.getSubject())
                .build();
        } catch (Exception e) {

            DecodedJWT decode = JWT.decode(token);

            return VerifyResult.builder()
                .success(false)
                .username(decode.getSubject())
                .build();
        }
    }
}
