package com.stove.drm.adapter.biz.service;

import com.stove.drm.adapter.biz.module.DrmService;
import com.stove.drm.adapter.biz.module.FileManagerService;
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
    
    public byte[] encrypt(String fileName, byte[] inputBytes) throws Exception {
         //01. 암호화대상 여부확인
         boolean val = drmService.checkExt(fileName);
         if (val == false)
             return null;                        
         
         //02. 임시파일 생성
         Path srcPath = fileManagerService.createFile(fileName, inputBytes);
         
         //03. 암호화 진행
        byte[] encFile = null;
        try {
            encFile = drmService.encrypt(srcPath, fileName+"_Enc");
            fileManagerService.clearTmpDir(srcPath);
            return encFile;
        } catch (Exception e) {
            fileManagerService.clearTmpDir(srcPath);
            throw new Exception("암호화파일 이상.[]");
        }
    } 
    
    public byte[] decrypt(String fileName, byte[] inputBytes) throws Exception {
        //01. 임시파일 생성  
        Path srcPath = fileManagerService.createFile(fileName,inputBytes);
        
        //02. 암호화 여부 확인 
        boolean rst =  drmService.isEncrypted(srcPath);
        
        //03. 복호화 요청 
        if (rst == true){
            byte[] decFile = drmService.decrypt(srcPath, "decrypt");
            fileManagerService.clearTmpDir(srcPath);
            return decFile;
        } else{
            throw new Exception("-36");
        }

       
    }
}
