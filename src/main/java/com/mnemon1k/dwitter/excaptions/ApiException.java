package com.mnemon1k.dwitter.excaptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ApiException {
    private long timestamp = new Date().getTime();
    private int statusCode;
    private String message;
    private String url;
    private Map<String, String> validationErrors;

    public ApiException(int statusCode, String message, String url) {
        this.statusCode = statusCode;
        this.message = message;
        this.url = url;
    }
}
