package com.stove.drm.adapter.biz.module;

import org.springframework.stereotype.Service;

@Service
public class JwtService  {

    public boolean isValid(String authorizationHeader) {
        return true;
    }
}
