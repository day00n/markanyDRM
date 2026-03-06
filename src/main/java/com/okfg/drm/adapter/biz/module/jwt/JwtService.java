package com.okfg.drm.adapter.biz.module.jwt;

import com.okfg.drm.adapter.biz.exception.DRMJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService  {

    private final JWTParser jwtParser;

    public boolean isValid(String authorizationHeader) {
        try {
            if (authorizationHeader == null) {
                log.warn("[JwtService]JWT String is null");
                return false;
            }
            authorizationHeader = authorizationHeader.replace("Bearer ", "");
            jwtParser.parseJwt(authorizationHeader);
        } catch (DRMJwtException e) {
            log.warn("[JwtService] {} {}",e.getCode(),e.getDesc());
            return false;
        }
        return true;
    }
}
