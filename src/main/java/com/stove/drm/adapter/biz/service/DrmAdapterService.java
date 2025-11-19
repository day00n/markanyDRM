package com.stove.drm.adapter.biz.service;

import com.stove.drm.adapter.biz.exception.DRMException;
import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.FileManagerService;
import com.stove.drm.adapter.biz.module.vo.DrmErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.rmi.server.ExportException;

@Service
@RequiredArgsConstructor
public class DrmAdapterService {

    private final DrmService drmService;
    private final FileManagerService fileManagerService;

    public boolean isEncrypted(String fileName, byte[] inputBytes) {
        //01. 임시파일 생성
        Path path = fileManagerService.createFile(fileName,inputBytes);
        //02. 암호화 여부 확인
        boolean rst = drmService.isEncrypted(path);            
        //03. 임시파일 삭제
        fileManagerService.clearTmpDir(path);
        return rst; 
    }
    
    public byte[] encrypt(String fileName, byte[] inputBytes) throws DRMException {
         //01. 암호화대상 여부확인
         boolean val = drmService.checkExt(fileName);
         if (!val) return inputBytes   ; // 원본 반환 필요 
         
         //02. 임시파일 생성
         Path srcPath = fileManagerService.createFile(fileName, inputBytes);
         
         //03. 암호화 진행
        try {
            return drmService.encrypt(srcPath, fileName+"_Enc");
        } finally {
            fileManagerService.clearTmpDir(srcPath);
        }
    } 
    
    public byte[] decrypt(String fileName, byte[] inputBytes) throws DRMException{
        //01. 임시파일 생성  
        Path srcPath = fileManagerService.createFile(fileName,inputBytes);
        
        //02. 암호화 여부 확인 
        boolean rst =  drmService.isEncrypted(srcPath);
        if (!rst) {
            throw new DRMException(DrmErrorEnum.ERROR_FILE_NOT_ENCRYPTED.value());
        }
        
        //03. 복호화 요청
        try {
            return drmService.decrypt(srcPath, "decrypt");
        }finally {
            fileManagerService.clearTmpDir(srcPath);
        }
    }
}
