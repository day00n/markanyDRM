package com.okfg.drm.adapter.biz.controller.vo;

import java.util.HashMap;
import java.util.Map;

public enum DoorayHeader {
    already_decrypted("Dooray-Drm-Result","already-decrypted"),
    already_encrypted("Dooray-Drm-Result","already-encrypted"),
    failed_to_decrypt("Dooray-Drm-Result","failed-to-decrypt"),
    failed_to_encrypt("Dooray-Drm-Result","failed-to-encrypt"),
    undecryptable("Dooray-Drm-Result","undecryptable");

    private final String key;
    private final String value;

    DoorayHeader(String key, String value) {
        this.key = key;
        this.value = value;
    }
    public Map<String,String> genMap(){
        Map<String, String> map = new HashMap<>();
        map.put(this.key, this.value);
        return map;
    }
}
