package com.stove.drm.adapter.biz.service;

import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.FileManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class DrmAdapterService {

    private final DrmService drmService;
    private final FileManagerService fileManagerService;

    public boolean isEncrypted(String fileName,byte[] inputBytes) {
        //01. 임시파일 생성
        Path path = fileManagerService.createFile(fileName,inputBytes);
        //02. 암호화 여부 확인
        boolean rst =  drmService.isEncrypted(path);
        //03. 임시파일 삭제
        fileManagerService.clearTmpDir(path);
        return rst;
    }
}
