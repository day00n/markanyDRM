package com.stove.drm.adapter.biz.module.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stove.drm.adapter.biz.exception.DRMJwtException;
import com.stove.drm.adapter.biz.module.jwt.vo.StoveUserVo;
import com.stove.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 인증 토큰 요청 파서
 * <p>
 * dooray -> 고객사로 인증 요청 토큰을 파싱
 * </p>
 *
 * @author myeongju.jung
 */
@Service
@RequiredArgsConstructor
public class JWTParser {

    private final DrmProp drmProp;

    public StoveUserVo getUserFromJWT(String tokenString) throws DRMJwtException {
        return new StoveUserVo().convert(parseJwt(tokenString));
    }

    public JWTClaimsSet parseJwt(String tokenString) throws DRMJwtException {
        try {
            SignedJWT jwt = SignedJWT.parse(tokenString);
            checkToken(jwt, tokenString);
            return jwt.getJWTClaimsSet();
        } catch (ParseException e) {
            String errorCode="JWT ParseException";
            throw new DRMJwtException(errorCode,tokenString);
        } catch (JOSEException e) {
            String errorCode="JWT JOSEException";
            throw new DRMJwtException(errorCode,tokenString);
        }
    }

//    public void checkToken(String tokenString) throws DRMJwtException {
//        try {
//            SignedJWT jwt = SignedJWT.parse(tokenString);
//            checkToken(jwt, tokenString);
//        } catch (ParseException e) {
//            String errorCode="JWT ParseException";
//            throw new DRMJwtException(errorCode,tokenString);
//        } catch (JOSEException e) {
//            String errorCode="JWT JOSEException";
//            throw new DRMJwtException(errorCode,tokenString);
//        }
//    }



    private void checkToken(SignedJWT jwt, String tokenString) throws JOSEException, ParseException, DRMJwtException {
        checkSignature(jwt, tokenString);
        checkExpired(jwt, tokenString);
    }

    private void checkSignature(SignedJWT jwt, String tokenString) throws JOSEException, DRMJwtException {
        JWSVerifier verifier = new MACVerifier(drmProp.getJwt().getSecret());
        if (!jwt.verify(verifier)) {
            String errorCode="JWT VERIFY";
            throw new DRMJwtException(errorCode,tokenString);
        }
    }

    private void checkExpired(SignedJWT jwt, String tokenString) throws ParseException, DRMJwtException {
        Date issuedAt = jwt.getJWTClaimsSet().getIssueTime();
        Date now = new Date();
        Instant expiredAt = issuedAt.toInstant().plus(10, ChronoUnit.MINUTES);
        if (now.toInstant().isAfter(expiredAt)) {
            String errorCode="JWT Expired";
            throw new DRMJwtException(errorCode,"[Expired At : "+expiredAt+"]"+tokenString);
        }
    }
}