package com.okfg.drm.adapter.biz.controller.vo.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DrmFileReq {
    @Schema(description = "DRM 라벨 정보", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drmLabel;

    @Schema(description = "암/복호화 대상 파일", type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;
}
