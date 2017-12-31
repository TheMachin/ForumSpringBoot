package org.miage.m2.forum.formValidation.annotation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FieldsValueMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsValueMatch {
    //http://www.baeldung.com/spring-mvc-custom-validator
    String message() default "Fields values don't match!";
    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String field();

    String fieldsMatch();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List{
        FieldsValueMatch[] value();
    }

}
