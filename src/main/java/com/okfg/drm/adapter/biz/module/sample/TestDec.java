package com.stove.drm.adapter.biz.module.sample;

import SCSL.*;

public final class TestDec
{
	public static void main(String[] args) 
	{
		String srcFile,dstFile;

		srcFile="/home/dooray/ServiceLinker_File/03_Sample/test_Enc.xls";
		dstFile="/home/dooray/ServiceLinker_File/03_Sample/test_Dec.xls";
	
		SLDsFile sFile = new SLDsFile();

		sFile.SettingPathForProperty("/home/dooray/ServiceLinker_File/02_Module/02_ServiceLinker/softcamp.properties"); 
		
		int retVal = sFile.CreateDecryptFileDAC (
		"/home/dooray/ServiceLinker_File/04_KeyFile/keyDAC_SVR0.sc",
		"SECURITYDOMAIN",
		srcFile,
		dstFile);
		System.out.println("CreateDecryptFileDAC [" + retVal + "]");
	}
}
