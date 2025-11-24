package com.stove.drm.adapter.biz.module;

import com.stove.drm.adapter.biz.exception.DRMJwtException;
import com.stove.drm.adapter.biz.module.jwt.JWTParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService  {

    private final JWTParser jwtParser;

    public boolean isValid(String authorizationHeader) {
        try {
            jwtParser.parseJwt(authorizationHeader);
        } catch (DRMJwtException e) {
            return true;
        }
        return false;
    }
}
