package com.mnemon1k.dwitter.shared;

import com.mnemon1k.dwitter.File.FileService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Base64;

public class ProfileImageValidator implements ConstraintValidator<ProfileImage, String> {
    FileService fileService;

    @Autowired
    public ProfileImageValidator(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    public void initialize(ProfileImage constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null)
            return true;

        byte[] bytes = Base64.getDecoder().decode(value);
        String type = fileService.detectType(bytes);

        if (type.equalsIgnoreCase("image/png") || type.equalsIgnoreCase("image/jpg"))
            return true;

        return false;
    }
}
