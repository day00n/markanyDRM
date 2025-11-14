package com.stove.drm.adapter.biz.module.vo;

import lombok.Getter;

@Getter
public enum DrmErrorEnum {

    // -------------------------------
    // 1. 기본 오류 (RETVAL / ERROR 계열)
    // -------------------------------

    UNDEFINED(999999, "정의안된 코드[%s]"),
    FILE_IO(999998, "정의안된 코드[%s]"),

    RETVAL_FAIL(1, "키 정보 오류일 경우"),
    ERROR_FAIL_CODE(-1, "파라미터가 NULL일 경우"),
    ERROR_MEM_ALLOCATE(-11, "메모리 할당 에러"),
    ERROR_MEM_ACCESS(-12, "메모리 접근 에러"),
    ERROR_SECU_ALG(-21, "암호화 알고리즘 에러"),
    ERROR_SECU_KEY(-22, "암호화 키 관련 에러"),
    ERROR_GET_ACL(-26, "ACL을 얻어오지 못함"),
    ERROR_RIGHT_ACL(-27, "올바른 ACL이 아님"),
    ERROR_FILE_EXIST(-31, "원본 파일이 존재하지 않음"),
    ERROR_FILE_CREATE(-32, "파일 생성 에러"),
    ERROR_FILE_ACCESS(-33, "파일 접근 에러"),
    ERROR_FILE_NOT_ENCRYPTED(-36, "원본 파일이 암호화 파일이 아님"),
    ERROR_FILE_DAMAGED(-41, "파일 손상됨"),
    NO_SCDSFILE_ACCESS(-51, "SCDS 파일 접근 불가"),
    ERROR_EXCEPTION(-61, "EXCEPTION 에러"),
    ERROR_FILESIZE_ZERO(-62, "원본 파일 사이즈 0"),
    ERROR_EXT_IMPOSSIBLE(-71, "지원하지 않는 확장자"),
    ERROR_USER_NOT_FOUND(-72, "입력 유저 ID를 찾을 수 없음"),
    ERROR_LOG_FAIL(-73, "로그 생성 실패"),
    ERROR_INDEXINFO(-75, "Index 검색 중 관련 에러"),
    ERROR_NOT_DEFINE_INDEXINFO(-76, "정의되지 않은 Index 사용"),
    RETVAL_BYPASS(-81, "BYPASS 모드"),
    ERROR_STREAM_SIZE_ZERO(-91, "스트림 사이즈 0"),
    STREAM_NOT_ENCRYPTED(-92, "스트림이 암호화 파일이 아님"),

    // -------------------------------
    // 2. ServiceLinker 에러 (1000번대)
    // -------------------------------

    NO_SOURCE_FILE_EXISTS(1000, "원본 파일이 없음"),
    SOURCE_FILESIZE_ZERO(1001, "원본 파일 크기가 0"),
    UNKNOWN_TYPE_OF_FILE(1002, "암호화 파일 방식 알 수 없음"),
    ENCFILE_SIZE_FAIL(1003, "암호화 파일 크기 오류"),
    ENCFILE_IS_NOT_DAC(1004, "암호화 파일이 DAC이 아님"),
    ENCFILE_IS_NOT_MAC(1005, "암호화 파일이 MAC이 아님"),
    ENCFILE_IS_NOT_GRADE(1006, "암호화 파일이 GRADE가 아님"),
    ERROR_ENCFILE_HASH_VALUE(1010, "암호화파일 Hash Value 불일치"),
    NO_MATCH_HEADER_KEY(1020, "헤더 복호화 실패"),

    // -------------------------------
    // 3. 파라미터 에러 (2000번대)
    // -------------------------------

    NULL_PARAMETER(2000, "파라미터가 NULL"),
    INVALID_PARAMETER(2001, "파라미터가 유효하지 않음"),
    SAME_FILEPATH(2002, "원본/대상 파일 경로가 동일함"),

    // -------------------------------
    // 4. 키 파일 관련 에러 (3000번대)
    // -------------------------------

    NO_KEYFILE_EXISTS(3000, "키 파일 없음"),
    NO_SEARCH_IDs_KEY(3001, "키 파일에 사용자 ID가 없음"),
    MAKE_DOCUMENT_KEY_FAIL(3002, "문서 암호화키 생성 실패"),
    NOT_FOUND_KEYFILE(3003, "서버 ID에 해당하는 키 파일 없음"),
    DIFFERENT_TYPE_OF_KEYFILE(3010, "키 파일 타입 다름"),
    KEYFILE_IS_NOT_DAC(3011, "키 파일이 DAC이 아님"),
    KEYFILE_IS_NOT_MAC(3012, "키 파일이 MAC이 아님"),
    KEYFILE_IS_NOT_GRADE(3013, "키 파일이 GRADE가 아님"),
    UNKNOWN_TYPE_OF_KEYFILE(3014, "키 파일 타입 알 수 없음"),
    KEYFILE_PARSING_FAIL(3020, "키 파일 파싱 실패"),
    KEYFILE_HEADER_INFO_FAIL(3021, "키 파일 헤더 정보 오류"),
    KEYFILE_SIZE_FAIL(3022, "키 파일 크기 오류"),
    NOT_AUTHENTICATION_KEYFILE(3030, "인증되지 않은 키 파일"),
    KEYFILE_DECRYPT_FAIL(3040, "키 파일 복호화 실패"),
    NOT_SUPPORT_ALGORITHM(3050, "알고리즘에 필요한 키 정보 없음"),

    // -------------------------------
    // 5. 암·복호화 에러 (4000번대)
    // -------------------------------

    DECRYPT_FAIL(4001, "복호화 실패"),
    NO_SUCH_ALGORITHM(4010, "암호화 알고리즘을 찾을 수 없음"),
    INVALID_KEY(4020, "지정된 키로 초기화 불가"),
    INVALID_ARGUMENT(4030, "부적절한 인자"),
    ERROR_ALGORITHM(4040, "블록/패딩 암복호화 오류");

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
