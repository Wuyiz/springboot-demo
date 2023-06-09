package com.example.common.handle;

import cn.hutool.core.util.StrUtil;
import com.example.common.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理类
 *
 * @author wuyiz
 * @Date 2023-07-03
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdviceHandle {

    @ExceptionHandler({Exception.class})
    public ResponseResult<String> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(String.format("_UncaughtException %s ：", request.getRequestURI()), e);
        return ResponseResult.error();
    }

    /**
     * BindException：form-data、x-www-form-urlencoded方式绑定到Java Bean时，bean内属性校验失败，抛出该异常
     * GET：url拼接、form-data
     * <p>
     * MethodArgumentNotValidException：基于json提交时，参数校验失败，抛出该异常
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<String> methodArgumentNotValidErrorHandler(Exception e) {
        BindingResult bindingResult;
        if (e instanceof MethodArgumentNotValidException) {
            bindingResult = ((MethodArgumentNotValidException) e).getBindingResult();
        } else {
            bindingResult = ((BindException) e).getBindingResult();
        }
        FieldError fieldError;
        List<String> errorArr = new ArrayList<>();
        for (ObjectError error : bindingResult.getAllErrors()) {
            if (error instanceof FieldError) {
                fieldError = (FieldError) error;
                errorArr.add(fieldError.getDefaultMessage());
            } else {
                errorArr.add(error.getDefaultMessage());
            }
            // break;  // 每次只展示一种校验错误
        }
        return ResponseResult.error(StrUtil.join(";", errorArr));
    }

    /**
     * ConstraintViolationException：凡是校验注解(@NotBlank .etc)放置在接口参数的，校验不通过时抛出此异常
     * <p>注意：须在控制层添加@Validated或参数前添加@Valid注解，使校验规则生效</>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        List<String> errorArr = new ArrayList<>();
        for (ConstraintViolation<?> constraint : e.getConstraintViolations()) {
            PathImpl path = (PathImpl) constraint.getPropertyPath();
            errorArr.add(path.getLeafNode().getName() + constraint.getMessage());
            break;  // 每次只展示一种校验错误
        }
        return ResponseResult.error("参数非法：" + StrUtil.join(";", errorArr));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<String> httpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error("_HttpMessageNotReadableException {} ：{}", request.getRequestURI(), e.getMessage());
        return ResponseResult.error("请求体格式不正确");
    }

    /**
     * MissingServletRequestParameterException：@RequestParam且require属性值为'true'，调用接口时必须传递参数名称，否则抛出此异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<String> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return ResponseResult.error(String.format("缺少必要参数%s", e.getParameterName()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult<String> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        return ResponseResult.error(String.format("参数%s类型错误", e.getName()));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseResult<String> duplicateKeyExceptionHandler(HttpServletRequest request, DuplicateKeyException e) {
        log.error("_DuplicateKeyException {} ：{}", request.getRequestURI(), e.getMessage());
        return ResponseResult.error("数据重复，请检查后提交");
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseResult<String> sqlExceptionHandler(HttpServletRequest request, DuplicateKeyException e) {
        log.error("_SQLException {} ：{}", request.getRequestURI(), e.getMessage());
        return ResponseResult.error();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseResult<String> httpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException e) {
        return ResponseResult.error(e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ResponseResult<String> httpMediaTypeNotSupportedExceptionHandle(HttpMediaTypeNotSupportedException e) {
        return ResponseResult.error(e.getMessage());
    }

}