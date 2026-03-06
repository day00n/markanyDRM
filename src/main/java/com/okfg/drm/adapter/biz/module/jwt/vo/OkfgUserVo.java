package com.okfg.drm.adapter.biz.module.jwt.vo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Data;

@Data
public class OkfgUserVo {
    private String userId;
    private String sessionId;
    private String userCode;
    private String name;
    private String dept;

    public OkfgUserVo convert(JWTClaimsSet claims) {
        ObjectMapper mapper = new ObjectMapper();
        String json = claims.toJSONObject().toJSONString();
        try {
            return mapper.readValue(json, OkfgUserVo.class);
        } catch (JsonProcessingException e) {
            return new OkfgUserVo();
        }
    }

}
