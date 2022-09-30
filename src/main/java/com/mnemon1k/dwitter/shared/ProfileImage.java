package com.mnemon1k.dwitter.shared;

import com.mnemon1k.dwitter.User.UniqueUsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = ProfileImageValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProfileImage {

    String message() default "{dwitter.constraints.image.FileFormat.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
