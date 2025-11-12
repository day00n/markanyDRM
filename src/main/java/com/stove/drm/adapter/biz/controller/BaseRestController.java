/**
 * 
 */
package com.stove.drm.adapter.biz.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * rest controller을 공통으로 관리한다.
 * @author Administrator
 *
 */
@RestController
public class BaseRestController {


	
	/**
	 * 성공
	 */
	protected ResponseEntity<Object> success(Object data) {
		/**
		 * 로그출력
		 */
		return new ResponseEntity<>(data, HttpStatus.OK);
	}


	protected ResponseEntity<Object> successWithHeader(Object data, HttpHeaders httpHeaders) {
		/**
		 * 로그출력
		 */
		return new ResponseEntity<>(data,httpHeaders, HttpStatus.OK);
	}




	/**
	 * 비즈니스 로직에 의해 데이타 정확성들의 문제가 발생시 Fail코드를 처리한다.
	 */
	protected ResponseEntity<Object> fail(String failMsg) {

		return new ResponseEntity<>(failMsg, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 비지니스 로직에 의한 에러발생시 Fail코드를 처리한다.
	 */
	protected ResponseEntity<Object> forbiden(Object errorInfoVo) {
		/**
		 * 로그출력
		 */
		return new ResponseEntity<>(errorInfoVo, HttpStatus.FORBIDDEN);
	}

	protected ResponseEntity<Object> error(Object errorInfoVo) {

		return new ResponseEntity<>(errorInfoVo, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Object processValidationError(MethodArgumentNotValidException ex) {
		return fail(ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
	}

	/**
	 * RuntimeException에 대한 처리.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(HttpServletRequest req, Exception ex) {
		return error(ex.getMessage());
	}



}
