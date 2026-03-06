package com.stove.drm.adapter.biz.module.sample;

import SCSL.*;

public final class TestEnc
{
	public static void main(String[] args) 
	{
		String srcFile,dstFile;

		srcFile="/home/dooray/ServiceLinker_File/03_Sample/test.xls";
		dstFile="/home/dooray/ServiceLinker_File/03_Sample/test_Enc.xls";
	
		SLDsFile sFile = new SLDsFile();

		sFile.SettingPathForProperty("/home/dooray/ServiceLinker_File/02_Module/02_ServiceLinker/softcamp.properties"); 
		
		sFile.SLDsInitDAC();                                                 
		sFile.SLDsAddUserDAC("SECURITYDOMAIN", "111001100", 0, 0, 0); 
    
		int ret;
		ret = sFile.SLDsEncFileDACV2("/home/dooray/ServiceLinker_File/04_KeyFile/keyDAC_SVR0.sc", "System", srcFile, dstFile, 1);                             
		System.out.println("SLDsEncFileDAC :" + ret);
	
	}
}

