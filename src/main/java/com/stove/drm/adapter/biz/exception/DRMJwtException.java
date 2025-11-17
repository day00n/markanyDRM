package com.stove.drm.adapter.biz.exception;

import com.stove.drm.adapter.biz.exception.base.BaseException;
import com.stove.drm.adapter.biz.module.vo.DrmErrorEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DRMJwtException extends BaseException {

    public DRMJwtException(String code,String desc){
        super(code,code, desc);
    }

}
