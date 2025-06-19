package com.technokratos.annotation;

import com.technokratos.validator.FileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileSizeValidator.class)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileSize {

    String message() default "Размер файла превышает допустимый лимит";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long max();
}
