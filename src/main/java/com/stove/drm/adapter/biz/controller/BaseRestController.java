/**
 * 
 */
package com.stove.drm.adapter.biz.controller;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
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



	//헤더 만들기


	
	private HttpHeaders generateFileHeader(byte[] file, String fileName){
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
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
