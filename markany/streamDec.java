import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import MarkAny.MaSaferJava.Madec;

public class streamDec{
	public static void main( String[ ] args ){

		if( args.length != 2 )
		{
			System.out.println("usage: java streamDn eid cid args=[" + String.valueOf(args.length) + "]");
			return;
		}

		String  strCompanyId = new String( args[1] );
		String  strEnterpriseID = new String( args[0] );

		Madec clMadec = null; // 클래스 생성 준비
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		String strRetCode = "";
		long OutFileLength = 0;

		// Sample Parameter
		String FileName = new String( "MarkAny" ); // 복호화 파일 명( 임의의 값 )
		File FileSample = new File( "./test_enc.docx" ); // 복호화 대상 파일
		File FileOut = new File( "./test_enc_dec.docx" ); // 복호화 결과로 생성할 파일

		if( FileSample.length( ) == 0 )
		{
			System.out.println( "ERR 파일크기 에러입니다." );
			return;
		}

		try
		{
			in = new BufferedInputStream( new FileInputStream( FileSample ) );
			out = new BufferedOutputStream( new FileOutputStream( FileOut ) );
		}
		catch( Exception e )
		{
			System.out.println( "스트림 객체 생성 에러입니다." );
			return;
		}

		// create instance
		// 복호화 클래스 생성
		try
		{
			clMadec = new Madec( "MarkAnyDrmInfo.dat" ); // 연동 시 절대경로로 변경
		}
		catch( Exception e )
		{
			System.out.println( "마크애니 복호화 클래스 생성 에러입니다." );
			System.out.println( "MarkAnyDrmInfo.dat 파일의 경로와 권한을 확인 해 주세요." );
			return;
		}

		// 복호화 대상 파일의 크기를 가져옵니다.
		long lFileLen = FileSample.length( );

		// 복호화 및 파라미터 점검을 합니다.
		try
		{
			OutFileLength = clMadec.lGetDecryptFileSize( FileName, lFileLen, in );
		}
		catch( Exception e )
		{
			System.out.println( "lGetDecryptFileSize 복호화 메소드 Exception Error. Exception = [" + e.toString( ) + "]" );
			System.out.println( "NumberFormat, NullPointer Exception일 경우 MarkAnyDrmInfo.dat 파일의 경로와 권한을 확인 해 주세요." );
			return;
		}

		// 복호화 준비를 합니다.
		if( OutFileLength > 0 )
		{
			// 복호화 합니다.
			strRetCode = clMadec.strMadec( out );
		}
		else // 복호화 시작전 에러가 발생했습니다.
		{
			System.out.println( "복호화 시작 전에 실패 하였습니다." );
			strRetCode = clMadec.strGetErrorCode( );
			System.out.println( "ERR [ErrorCode] = [" + strRetCode + "]"
					+ "[ErrorDescription] = ["
					+ clMadec.strGetErrorMessage(strRetCode) + "]" );
			return;
		}

		if( strRetCode.equals( "00000" ) )
		{
			// 복호화를 성공했습니다.
			System.out.println( "복호화에 성공 하였습니다." );
			System.out.println( "RetCode = [" + strRetCode + "]" );
		}
		else
		{
			// 복호화에 실패했습니다.
			FileOut.delete( );
			System.out.println( "복호화에 실패 하였습니다." );
			System.out.println( "ERR [ErrorCode] = [" + strRetCode + "]"
					+ "[ErrorDescription] = ["
					+ clMadec.strGetErrorMessage(strRetCode) + "]" );
		}
		return;
	}
}
