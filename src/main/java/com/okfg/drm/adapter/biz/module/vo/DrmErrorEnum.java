package com.okfg.drm.adapter.biz.module.vo;

import lombok.Getter;

@Getter
public enum DrmErrorEnum {

    /* =========================
     * SUCCESS
     * ========================= */
    SUCCESS(0, "처리 성공"),
    SUCCESS_ALT(00000, "처리 성공"),


    /* =========================
     * NETWORK / 통신 오류
     * ========================= */
    SERVER_CONNECTION_ERROR(10001, "서버 접속 에러 (네트워크 문제 또는 데몬 중지 상태 또는 서버 정보 IP/PORT 변경)"),

    SOCKET_WRITE_ERROR(60002, "socket 쓰기 에러"),
    SOCKET_ERROR_1(60003, "socket 에러"),
    SOCKET_READ_ERROR(60004, "socket 읽기 에러"),
    SOCKET_ERROR_2(60005, "socket 에러"),
    PACKET_ERROR(60006, "packet 에러"),


    /* =========================
     * PARAMETER / 입력값 오류
     * ========================= */
    ARGUMENT_ERROR(60007, "아규먼트 에러"),
    SETDATA_INVALID_TAGNUM(60014, "setdata()에서 유효하지 않는 tagnum"),
    GETDATA_INVALID_TAGNUM(60015, "getdata()에서 유효하지 않는 tagnum"),

    INVALID_STRING_DATA(60016, "String 데이터가 유효하지 않음"),
    INVALID_SIZE_STRING_DATA(60017, "크기 문자열 데이터가 유효하지 않음"),
    DUMMY_DATA_ERROR(60018, "dummy 데이터 오류"),

    USER_ID_ERROR(60046, "user id 오류"),
    COMPANY_ID_ERROR(60047, "company id 오류"),
    ENTERPRISE_ID_ERROR(60048, "enterprise id 오류"),
    INVALID_USER_ID_IN_DECRYPT_PARAM(61001, "복호화 연동 파라미터 중 유효하지 않는 user id"),


    /* =========================
     * IO / STREAM 오류
     * ========================= */
    INPUT_STREAM_ERROR(60008, "inputstream 에러"),
    INPUT_STREAM_AVAILABLE_SIZE_ERROR(60009, "inputstream 사용가능한 크기 에러"),
    OUTPUT_STREAM_ERROR(60010, "outputstream 에러"),
    INPUT_STREAM_SIZE_ERROR(60039, "inputstream 크기 오류"),
    STREAM_ERROR(60049, "inputstream or outputstream 오류"),
    BYTE_BUFFER_SIZE_ERROR(60050, "byte buffer 크기 오류"),


    /* =========================
     * CONFIG / DAT 파일 오류
     * ========================= */
    SERVER_INFO_DAT_FILE_ERROR(60011, "서버 정보 dat파일 오류"),
    SERVER_INFO_DAT_CREATED_BY_ERROR(60012, "서버 정보 dat파일 created by 오류"),


    /* =========================
     * DRM TYPE / 정책 오류
     * ========================= */
    DRM_TYPE_ERROR(60013, "drm 기능 type 오류"),
    DRM_STATUS_SEND(60030, "drm 상태 전송"),
    DRM_PROPERTY_GET_ERROR(60040, "drm 속성 가져오기 오류"),
    DRM_PROPERTY_GET_EXCEPTION(60041, "drm 속성 가져오기 예외처리"),
    DRM_BLUE3_ENCRYPTED_FILE(60042, "DRM Blue3 암호화 파일 (암호화 파일을 암호화 시도한 경우)"),
    DRM_CIPHER_30_ENCRYPTED_FILE(60043, "DRM cipher 3.0 암호화 파일"),
    PLAIN_FILE_ON_DECRYPT_REQUEST(60045, "평문 파일 복호화 상태 (복호화 요청 시 입력된 파일이 평문 파일)"),
    NO_DECRYPT_PERMISSION(61002, "복호화 권한이 없어 복호화 중지"),


    /* =========================
     * DRM HEADER / 파일 구조 오류
     * ========================= */
    BLUE_SIGNATURE_READ_ERROR(60019, "DRM 암호화 파일 Blue Signature 읽기 오류"),

    BASE_HEADER_SIZE_ERROR(60020, "base header 크기 오류"),
    BASE_HEADER_ERROR(60021, "base header 오류"),
    ENCRYPTED_BASE_HEADER_ERROR(60022, "암호화된 base header 오류"),

    CONTENTS_HEADER_ERROR(60023, "contents header 오류"),
    ENCRYPTED_CONTENTS_HEADER_ERROR(60024, "암호화된 contents header 오류"),

    FILE_SIZE_PADDING_ERROR(60025, "파일크기와 패딩크기 오류"),
    ENCRYPTED_FILE_ERROR(60026, "암호화 파일 오류"),
    INVALID_FILE_PADDING_SIZE(60036, "유효하지 않은 파일 패딩크기 오류"),
    FILE_SIZE_PROCESS_ERROR(60037, "파일 크기 처리 오류"),
    DECRYPTION_ERROR(60038, "복호화 오류"),


    /* =========================
     * DRM TRANSFER / 전송 오류
     * ========================= */
    SIGNATURE_SEND_ERROR(60027, "시그너처 전송 오류"),
    ENCRYPTED_BASE_HEADER_SIZE_SEND_ERROR(60028, "암호화된 base header 크기 전송 오류"),
    BASE_HEADER_SIZE_SEND_ERROR(60029, "base header 크기 전송 오류"),
    BASE_HEADER_SIZE_EXCEPTION(60031, "base header 크기 예외처리"),
    BASE_HEADER_SEND_ERROR(60032, "base header 전송 오류"),

    CONTENTS_HEADER_SIZE_SEND_ERROR(60033, "contents header 크기 전송 오류"),
    CONTENTS_HEADER_SIZE_EXCEPTION(60034, "contents header 크기 예외 처리"),
    CONTENTS_HEADER_SEND_ERROR(60035, "contents header 전송 오류"),


    /* =========================
     * STATUS / 기타 상태 코드
     * ========================= */
    FILE_COPY_STATUS(60044, "파일 복사 상태"),


    /* =========================
     * UNKNOWN
     * ========================= */
    UNDEFINED(999999, "정의안된 코드[%s]"),
    FILE_IO(999998, "정의안된 코드[%s]");



    private final int value;
    private final String description;

    DrmErrorEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int value() {
        return value;
    }

    public String description() {
        return String.format(description, value);
    }

    // code 값으로 enum 찾기
    public static DrmErrorEnum fromValue(int value) {
        for (DrmErrorEnum e : values()) {
            if (e.value == value) {
                return e;
            }
        }
        return DrmErrorEnum.UNDEFINED;
    }

    public static DrmErrorVo getDrmErrorVo(int value) {
        DrmErrorEnum drmErrorEnum = fromValue(value);
        if (drmErrorEnum == UNDEFINED) {
            return new DrmErrorVo(DrmErrorEnum.UNDEFINED,value);
        }
        return new DrmErrorVo(drmErrorEnum);
    }
}
