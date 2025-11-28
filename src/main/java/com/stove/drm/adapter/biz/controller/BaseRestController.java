/**
 * 
 */
package com.stove.drm.adapter.biz.controller;


import ch.qos.logback.core.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * rest controller을 공통으로 관리한다.
 * @author Administrator
 *
 */
@RestController
@Slf4j
public class BaseRestController {

	private HttpHeaders generateFileHeader(byte[] file, String fileName){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		//파일명이 없을 경우 빈값일 경우 NULL로 처리.
		String baseName = FilenameUtils.getBaseName(fileName);
		String extensionName = FilenameUtils.getExtension(fileName);

		if (StringUtils.isEmpty(baseName)) {
			baseName="null";
		}
		fileName = baseName + "." + extensionName;
		ContentDisposition contentDisposition = ContentDisposition.attachment().filename(fileName, StandardCharsets.UTF_8).build();
		headers.setContentDisposition(contentDisposition);
		headers.setContentLength(file.length);
		return headers;
	}

	/**
	 * 성공
	 */
	protected ResponseEntity<?> fileOk(byte[] file, String fileName) {
		return fileOkWithHeader(file,fileName,null);
	}
	protected ResponseEntity<?> fileOkWithHeader(byte[] file, String fileName, Map<String,String> headerData) {
		HttpHeaders headers = generateFileHeader(file, fileName);
		if (headerData != null && !headerData.isEmpty()) {
			headerData.forEach((k,v)->{
				headers.add(k,v);
			});
		}
		return   new ResponseEntity<>(new ByteArrayResource(file), headers, HttpStatus.OK);
	}

	protected ResponseEntity<?> fileFail(HttpStatus status,byte[] file,String fileName) {
		return fileFailWithHeader(status,file,fileName,null);
	}
	protected ResponseEntity<?> fileFailWithHeader(HttpStatus status,byte[] file,String fileName,Map<String,String> headerData) {
		HttpHeaders headers = generateFileHeader(file, fileName);
		if (headerData != null && !headerData.isEmpty()) {
			headerData.forEach((k,v)->{
				headers.add(k,v);
			});
		}
		return   new ResponseEntity<>(new ByteArrayResource(file), headers, status);
	}

	protected ResponseEntity<?> jsonOk(Object object) {
		return jsonOkWithHeader(object,null);
	}
	protected ResponseEntity<?> jsonOkWithHeader(Object object, Map<String, String> headerData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (headerData != null && !headerData.isEmpty()) {
			headerData.forEach((k,v)->{
				headers.add(k,v);
			});
		}
		return new ResponseEntity<>(object, headers, HttpStatus.OK);
	}

	protected ResponseEntity<?> jsonFail(HttpStatus status,Object object) {
		return jsonFailWithHeader(status, object, null);
	}
	protected ResponseEntity<?> jsonFailWithHeader(HttpStatus status,Object object, Map<String, String> headerData) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (headerData != null && !headerData.isEmpty()) {
			headerData.forEach((k,v)->{
				headers.add(k,v);
			});
		}

		return new ResponseEntity<>(object, headers, status);
	}

	@ExceptionHandler({MaxUploadSizeExceededException.class, MultipartException.class})
	public ResponseEntity<?> handleMultipartException(Exception ex) {

		String message = "업로드 가능한 파일 용량을 초과했습니다.";

		// 톰캣 SizeLimitExceededException 래핑 케이스
		if (ex.getCause() instanceof SizeLimitExceededException) {
			message = "업로드 가능한 용량을 초과했습니다.";
		}

		Map<String, Object> body = new HashMap<>();
		body.put("error", "FILE_SIZE_EXCEEDED");
		body.put("message", message);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
//      PAYLOAD_TOO_LARGE
		return new ResponseEntity<>(body, headers, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> processValidationError(MethodArgumentNotValidException ex) {
		log.error("[BaseController]{}",ex.getMessage());
		Map<String, String> map = new HashMap<>();
		map.put("msg", ex.getMessage());

		Map<String, String> header = new HashMap<>();
		header.put("Dooray-Drm-Result", "failed-to-decrypt");
		return jsonFailWithHeader(HttpStatus.INTERNAL_SERVER_ERROR,map,header);
	}

	/**
	 * RuntimeException에 대한 처리.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(HttpServletRequest req, Exception ex) {
		log.error("[BaseController]{}",ex.getMessage());
		Map<String, String> map = new HashMap<>();
		map.put("msg", ex.getMessage());

		Map<String, String> header = new HashMap<>();
		header.put("Dooray-Drm-Result", "failed-to-decrypt");
		return jsonFailWithHeader(HttpStatus.INTERNAL_SERVER_ERROR,map,header);
	}



}
