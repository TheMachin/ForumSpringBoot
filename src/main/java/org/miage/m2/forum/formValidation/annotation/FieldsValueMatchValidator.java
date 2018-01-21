package org.miage.m2.forum.formValidation.annotation;

import org.miage.m2.forum.config.internalization.MessageByLocaleServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch, Object> {
    //http://www.baeldung.com/spring-mvc-custom-validator
    private String field;
    private String fieldMatch;
    private String message;

    @Autowired
    MessageByLocaleServiceImpl messageByLocaleService;

    @Override
    public void initialize(FieldsValueMatch constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldsMatch();
        this.message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext cvc){
        boolean toReturn = false;

        try{
            final Object firstObj = new BeanWrapperImpl(value)
                    .getPropertyValue(field);
            final Object secondObj = new BeanWrapperImpl(value)
                    .getPropertyValue(fieldMatch);

            //System.out.println("firstObj = "+firstObj+"   secondObj = "+secondObj);

            toReturn = firstObj == null && secondObj == null || firstObj != null && firstObj.equals(secondObj);
        }
        catch (final Exception e){
            System.out.println(e.toString());
        }
        //If the validation failed
        if(!toReturn) {
            cvc.disableDefaultConstraintViolation();
            //In the initialiaze method you get the errorMessage: constraintAnnotation.message();
            cvc.buildConstraintViolationWithTemplate(messageByLocaleService.getMessage(message)).addPropertyNode(field).addConstraintViolation();
        }
        return toReturn;
    }
}
