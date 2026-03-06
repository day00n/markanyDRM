package com.okfg.drm.adapter.biz.controller.rest;

import com.okfg.drm.adapter.biz.controller.BaseRestController;
import com.okfg.drm.adapter.biz.module.jwt.JwtService;
import com.okfg.drm.adapter.biz.module.jwt.JWTGenerator;
import com.okfg.drm.adapter.biz.service.DrmAdapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "암복호화 두레이 DRM 어댑터.")
@RestController
@RequiredArgsConstructor
public class HeartBeatController extends BaseRestController {

    private final JWTGenerator jWTGenerator;   // Authorization Bearer 토큰 검증
    private final JwtService jwtService;   // Authorization Bearer 토큰 검증
    private final DrmAdapterService drmAdapterService;   // 암/복호화 어댑터 연동 (외부 DRM 서버 호출)

    @Operation(
            summary  = "heartBeat.",
            description  = "heartBeat"
    )
    @GetMapping(value = "/")
    public ResponseEntity<?> heartBeat(){
        Map<String, String> rst = new HashMap<>();
        rst.put("msg", "OK");
        return jsonOk(rst);
    }
}
