package com.luke.demo.constant;

/**
 * @Description:
 * @author: lulu
 * @Date: 2022/12/1 5:39 下午
 **/
public enum SexEnum {
    FEMALE("F","女"),
    MALE("M","男");

    private String code;
    private String desc;

    SexEnum(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
