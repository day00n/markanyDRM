package com.okfg.drm.adapter.biz.module.vo;

import lombok.Data;

@Data
public class DrmErrorVo {

    public String code;
    public int value;
    public String desc;

    public DrmErrorVo(DrmErrorEnum drmErrorEnum) {
        this.code = drmErrorEnum.name();
        this.value = drmErrorEnum.getValue();
        this.desc = drmErrorEnum.getDescription();
    }

    public DrmErrorVo(DrmErrorEnum drmErrorEnum,int value) {
        this.code = drmErrorEnum.name();
        this.value = value;
        this.desc = drmErrorEnum.getDescription();
    }
}
