package com.stove.drm.adapter.biz.module.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.stove.drm.adapter.biz.module.jwt.vo.StoveUserVo;
import com.stove.drm.adapter.core.config.DrmProp;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 인증 토큰 생성 콤포넌트
 *
 * <p>
 * 고객사 인증 완료 후 dooray에 전달하는 토큰 생성기
 * </p>
 *
 * @author myeongju.jung
 */
@Service
@RequiredArgsConstructor
public class JWTGenerator {

    private final DrmProp drmProp;

    public JWT toJwtToken(StoveUserVo authenticated) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(drmProp.getJwt().getIssuer()) //dooray.issuer 값 "spay" 두레이에서 제공.
                    .claim("userId", authenticated.getUserId())
                    .claim("session", authenticated.getSessionId())
                    .claim("userCode", authenticated.getUserCode())
                    .claim("name", authenticated.getName())
                    .claim("dept", authenticated.getDept())
                    .claim("timestamp", Calendar.getInstance().getTimeInMillis())//호출시 매번 다른 키를 생성하기 위해 timestamp를 넣고 생성한다.
                    .issueTime(new Date())
                    .build();

            JWSSigner signer = new MACSigner(drmProp.getJwt().getSecret());

            SignedJWT token = new SignedJWT(header, claimsSet);
            token.sign(signer);
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(Calendar.getInstance().getTimeInMillis());
        try {
            Thread.sleep(2000);
            System.out.println(Calendar.getInstance().getTimeInMillis());
//            System.out.println(LoginCheckUtils.aesCBCEncode("2171014"));
//            System.out.println(LoginCheckUtils.aesCBCEncode("21710141"));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
