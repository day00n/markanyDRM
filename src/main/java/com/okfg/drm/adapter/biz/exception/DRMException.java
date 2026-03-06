package com.okfg.drm.adapter.biz.exception;

import com.okfg.drm.adapter.biz.exception.base.BaseException;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorEnum;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorVo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DRMException extends BaseException {

    DrmErrorVo drmErrorVo;

    public DRMException(int value){
        super(String.valueOf(value), "", "");
        this.drmErrorVo = DrmErrorEnum.getDrmErrorVo(value);
    }

}
