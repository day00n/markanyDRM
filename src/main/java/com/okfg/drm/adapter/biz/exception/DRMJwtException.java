package com.stove.drm.adapter.biz.exception;

import com.stove.drm.adapter.biz.exception.base.BaseException;
import com.stove.drm.adapter.biz.module.vo.DrmErrorEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DRMJwtException extends BaseException {
    String code;
    String desc;

    public DRMJwtException(String code,String desc){
        super(code,code, desc);
        this.code = code;
        this.desc = desc;
    }

}
