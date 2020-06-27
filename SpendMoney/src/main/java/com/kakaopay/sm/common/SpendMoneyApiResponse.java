package com.kakaopay.sm.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
 * return 값 Json으로 통일
 */
@JsonTypeName(value = "Result") //전체 이름
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonAutoDetect(fieldVisibility = Visibility.ANY)   //Class 차원에서 ObjectMapper가 필드를 감지할 수 있도록 세팅
@Getter @Setter
@ToString
public class SpendMoneyApiResponse {
	private String code = "0000";
    private String message = "SUCCESS";
    private Object data;

    public SpendMoneyApiResponse () {
        // TODO Auto-generated constructor stub
    }

    public SpendMoneyApiResponse (Object data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
