package com.okfg.drm.adapter.biz.service;

import com.okfg.drm.adapter.biz.exception.DRMException;
import com.okfg.drm.adapter.biz.module.drm.markany.MarkanyDrmService;
//import com.okfg.drm.adapter.biz.module.drm.softcamp.FileManagerService;
//import com.okfg.drm.adapter.biz.module.drm.softcamp.SoftcampDrmService;
import com.okfg.drm.adapter.biz.module.drm.markany.MarkanyDrmService;
import com.okfg.drm.adapter.biz.module.vo.DrmErrorEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class DrmAdapterService {

    private final MarkanyDrmService markAnyDrmService;

    public boolean isEncrypted(String fileName, byte[] inputBytes) {
        return markAnyDrmService.isEncrypted(fileName, inputBytes);
    }
    
    public byte[] encrypt(String fileName, byte[] inputBytes) throws DRMException {
         return markAnyDrmService.encrypt(fileName, inputBytes);
    } 
    
    public byte[] decrypt(String fileName, byte[] inputBytes) throws DRMException{
        return markAnyDrmService.decrypt(fileName, inputBytes);
    }
}
