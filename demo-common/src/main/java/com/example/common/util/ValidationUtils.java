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
     * 校验对象，校验不通过抛出异常
     *
     * @param <T>    Bean类型
     * @param bean   被校验的Bean
     * @param groups 校验组
     */
    public static <T> void validate(T bean, Class<?>... groups) {
        Set<ConstraintViolation<T>> result = validator.validate(bean, groups);
        if (!result.isEmpty()) {
            throw new ConstraintViolationException(result);
        }
    }
}
