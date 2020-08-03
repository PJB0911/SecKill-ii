package com.gan.validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * 数据校验器
 */
@Component
public class ValidatorImpl implements InitializingBean {

    private Validator validator;

    @Override
    public void afterPropertiesSet() throws Exception {
        //将hibernate validator通过工厂的初始化方式使其实例化
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 实现校验方法并返回校验结果
     *
     * @param bean
     * @return
     */
    public ValidationResult validate(Object bean) {

        final ValidationResult result = new ValidationResult();

        Set<ConstraintViolation<Object>> constraintViolationSet = validator.validate(bean);
        //有错误
        if (constraintViolationSet.size() > 0) {
            result.setHasErrors(true);
            constraintViolationSet.forEach(constraintViolation -> {
                String errMsg = constraintViolation.getMessage();
                String propertyName = constraintViolation.getPropertyPath().toString();
                result.getErrorMsgMap().put(propertyName, errMsg);
            });
        }
        return result;
    }


}
