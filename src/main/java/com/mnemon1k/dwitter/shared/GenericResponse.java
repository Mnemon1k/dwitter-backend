package com.mnemon1k.dwitter.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.ConstructorProperties;

@Data
@NoArgsConstructor
public class GenericResponse {
    private String message;

    public GenericResponse(String message) {
        this.message = message;
    }
}
