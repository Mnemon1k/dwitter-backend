package com.mnemon1k.dwitter.excaptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestController
public class ExceptionHandler implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public ExceptionHandler(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    public String getErrorPath(){
        return "/error";
    }

    @RequestMapping("/error")
    ApiException handleException(WebRequest webRequest){
        Map<String, Object> attributes = errorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE));

        String message = (String) attributes.get("message");
        String url = (String) attributes.get("path");
        int status = (Integer) attributes.get("status");

        return new ApiException(status, message, url);
    }
}
