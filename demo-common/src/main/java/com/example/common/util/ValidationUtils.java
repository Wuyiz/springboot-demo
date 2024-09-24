package com.example.common.util;

import javax.validation.*;
import java.util.Set;

/**
 * JSR-303约束校验工具
 *
 * @author suhai
 * @since 2023-11-30
 */
public class ValidationUtils {
    private static final Validator validator;

    static {
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }
    }

    /**
     * 校验bean对象
     *
     * @param <T>    Bean类型
     * @param bean   bean
     * @param groups 校验组
     * @return {@link Set} 校验结果集
     */
    public static <T> Set<ConstraintViolation<T>> validate(T bean, Class<?>... groups) {
        return validator.validate(bean, groups);
    }

    /**
     * 校验bean的某一个属性
     *
     * @param <T>          Bean类型
     * @param bean         bean
     * @param propertyName 属性名称
     * @param groups       验证分组
     * @return {@link Set}
     */
    public static <T> Set<ConstraintViolation<T>> validateProperty(T bean, String propertyName, Class<?>... groups) {
        return validator.validateProperty(bean, propertyName, groups);
    }

    /**
     * 校验bean对象，校验不通过抛出异常{@link ConstraintViolationException}
     *
     * @param <T>    Bean类型
     * @param bean   被校验的Bean
     * @param groups 校验组
     */
    public static <T> void validateAndThrowException(T bean, Class<?>... groups) {
        Set<ConstraintViolation<T>> result = validate(bean, groups);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }

    /**
     * 校验bean对象的某一个属性，校验不通过抛出异常{@link ConstraintViolationException}
     *
     * @param <T>          Bean类型
     * @param bean         被校验的Bean
     * @param propertyName 属性名称
     * @param groups       校验组
     */
    public static <T> void validatePropertyAndThrowException(T bean, String propertyName, Class<?>... groups) {
        Set<ConstraintViolation<T>> result = validateProperty(bean, propertyName, groups);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }
}
