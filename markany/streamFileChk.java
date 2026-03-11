import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import MarkAny.MaSaferJava.MaFileChk;

public class streamFileChk{
	public static void main( String[ ] args ){
		if( args.length != 2 )
		{
			System.out.println("usage: java streamDn eid cid");
			return;
		}

		String  strCompanyId = new String( args[1] );
		String  strEnterpriseID = new String( args[0] );

		MaFileChk clMaFileChk = null; // 클래스 생성 준비
		BufferedInputStream in = null;
		String strRetCode = "";
		long OutFileLength = 0;

		// Sample Parameter
		String FileName = new String( "./test_enc.docx" ); // 파일체크 대상 파일
		File FileSample = new File( FileName ); // 파일체크 대상 파일

		if( FileSample.length() == 0 )
		{
			System.out.println("ERR 파일크기 에러입니다.");
			return;
		}

		try
		{
			in = new BufferedInputStream( new FileInputStream( FileSample ) );
		}
		catch( Exception e )
		{
			System.out.println("스트림 객체 생성 에러입니다.");
			return;
		}

		// create instance
		// 파일체크 클래스 생성
		try
		{
			clMaFileChk = new MaFileChk( "MarkAnyDrmInfo.dat" ); // 연동 시 절대경로로 변경
		}
		catch( Exception e )
		{
			System.out.println( "마크애니 파일체크 클래스 생성 에러입니다." );
			System.out.println( "MarkAnyDrmInfo.dat 파일의 경로와 권한을 확인 해 주세요." );
			return;
		}

		// 파일체크 대상 파일의 크기를 가져옵니다.
		long lFileLen = FileSample.length( );

		// 파일체크 및 파라미터 점검을 합니다.
		try
		{
			OutFileLength = clMaFileChk.lGetFileChkFileSize( FileName, lFileLen, in );
		}
		catch( Exception e )
		{
			System.out.println( "lGetFileChkFileSize 파일체크 메소드 Exception Error. Exception = [" + e.toString( ) + "]" );
			System.out.println( "NumberFormat, NullPointer Exception일 경우 MarkAnyDrmInfo.dat 파일의 경로와 권한을 확인 해 주세요." );
			return;
		}

		// 파일체크 준비를 합니다.
		if( OutFileLength > 0 )
		{
			// 파일을 체크 합니다.
			strRetCode = clMaFileChk.strMaFileChk( );
		}
		else // 파일체크 시작전 에러가 발생했습니다.
		{
			System.out.println( "파일체크 시작 전에 실패 하였습니다." );
			strRetCode = clMaFileChk.strGetErrorCode( );
			System.out.println( "ERR [ErrorCode] = [" + strRetCode + "]"
					+ "[ErrorDescription] = ["
					+ clMaFileChk.strGetErrorMessage(strRetCode) + "]" );
			return;
		}

		if( strRetCode.equals( "00000" ) )
		{
			// 파일체크를 성공하였습니다.
			System.out.println( "파일체크를 성공 하였습니다. 해당 파일은 암호화 파일 입니다." );
			// 암호 파일의 속성 체크
			System.out.println( "strGetUserID() = [" + clMaFileChk.strGetUserID() + "]" );
			System.out.println( "strGetMultiUserID() = [" + clMaFileChk.strGetMultiUserID() + "]" );
			System.out.println( "strGetMultiUserName() = [" + clMaFileChk.strGetMultiUserName() + "]" );
			System.out.println( "strGetFileID() = [" + clMaFileChk.strGetFileID() + "]" );
			System.out.println( "strGetEnterpriseID() = [" + clMaFileChk.strGetEnterpriseID() + "]" );
			System.out.println( "strGetCompanyID() = [" + clMaFileChk.strGetCompanyID() + "]" );
			System.out.println( "strGetGroupID() = [" + clMaFileChk.strGetGroupID() + "]" );
			System.out.println( "strGetDeptID() = [" + clMaFileChk.strGetDeptID() + "]" );
			System.out.println( "strGetDeptName() = [" + clMaFileChk.strGetDeptName() + "]" );
			System.out.println( "strGetPositionID() = [" + clMaFileChk.strGetPositionID() + "]" );
			System.out.println( "strGetPositionLevel() = [" + clMaFileChk.strGetPositionLevel() + "]" );
			System.out.println( "strGetSecurityLevel() = [" + clMaFileChk.strGetSecurityLevel() + "]" );
			System.out.println( "strGetDocumentGrade() = [" + clMaFileChk.strGetDocumentGrade() + "]" );
			System.out.println( "strGetDocExchangePolicy() = [" + clMaFileChk.strGetDocExchangePolicy() + "]" );
			System.out.println( "strGetMachineKey() = [" + clMaFileChk.strGetMachineKey() + "]" );
			System.out.println( "strGetDocumentKey() = [" + clMaFileChk.strGetDocumentKey() + "]" );
			System.out.println( "strGetCreatorID() = [" + clMaFileChk.strGetCreatorID() + "]" );
			System.out.println( "strGetCreatorCompanyID() = [" + clMaFileChk.strGetCreatorCompanyID() + "]" );
			System.out.println( "strGetCreatorDeptID() = [" + clMaFileChk.strGetCreatorDeptID() + "]" );
			System.out.println( "strGetCreatorGroupID() = [" + clMaFileChk.strGetCreatorGroupID() + "]" );
			System.out.println( "strGetCreatorPositionID() = [" + clMaFileChk.strGetCreatorPositionID() + "]" );
			System.out.println( "strGetCreateBy() = [" + clMaFileChk.strGetCreateBy() + "]" );
		}
		else
		{
			// 파일체크를 실패했습니다.
			System.out.println( "파일체크를 실패 하였습니다." );
			System.out.println( "ERR [ErrorCode] = [" + strRetCode + "]"
					+ "[ErrorDescription] = ["
					+ clMaFileChk.strGetErrorMessage(strRetCode) + "]" );
		}
		return;
	}
}
