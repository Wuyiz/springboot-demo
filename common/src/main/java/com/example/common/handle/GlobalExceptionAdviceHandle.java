package com.example.common.handle;

import com.example.common.domain.ResponseResult;
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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理类
 * <p>
 * 1.统一将异常请求的HTTP状态设置为OK_200，前端根据接口的响应数据处理业务，而不是根据HTTP状态处理<br/>
 * 2.这样做的好处是，可以在系统发生大面积异常时，前端页面的错误提示的数量和内容可控<br/>
 * 3.前端不应该直接展示接口异常提示，而是有选择性的根据响应码展示<br/>
 *
 * @author suhai
 * @since 2023-07-03
 */
@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.OK)
public class GlobalExceptionAdviceHandle {

    @ExceptionHandler({Exception.class})
    public ResponseResult<String> exceptionHandler(HttpServletRequest request, Exception e) {
        log.error(MessageFormat.format("_UncaughtException {0} ：", request.getServletPath()), e);
        return ResponseResult.error();
    }

    /**
     * BindException：form-data、x-www-form-urlencoded方式绑定到Java Bean时，bean内属性校验失败，抛出该异常
     * <p>
     * MethodArgumentNotValidException：基于json提交时，参数校验失败，抛出该异常
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
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


            // 只展示其中一种校验错误
            // break;
        }
        return ResponseResult.error(String.join(";", errorArr));
    }

    /**
     * ConstraintViolationException：参数才方法上而不是对象内，且存在@Validated和@NotBlank等JSR-303注解时，校验失败则抛出此异常
     * <p>
     * 例：当控制层接口的方法参数不在自定义的对象内，而是直接在方法上时，校验不通过才会抛出此异常；<br>
     * 例如，public void testApi(@RequestParam @NotBlank String name) {}
     * <p>
     * 注意：必须搭配@Validated注解，并且该注解需要在在控制层的类、或方法、或参数上，校验方能生效
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<String> constraintViolationExceptionHandler(ConstraintViolationException e) {
        List<String> errorArr = new ArrayList<>();
        for (ConstraintViolation<?> constraint : e.getConstraintViolations()) {
            PathImpl path = (PathImpl) constraint.getPropertyPath();
            errorArr.add(path.getLeafNode().getName() + constraint.getMessage());
            break;  // 每次只展示一种校验错误
        }
        return ResponseResult.error(String.join(";", errorArr));
    }

    /**
     * MissingServletRequestParameterException：请求接口时确实必传参数的名称，抛出此异常
     * <p>
     * 例：@RequestParam(require = true)下的参数，在请求接口时必须携带参数名称；<br>
     * 例如，在请求路径"/api/test?id=&name"中，如果id和name两个参数的require=true时，则在请求时必须携带参数的名称，否则抛出此异常；<br>
     * 如果参数的require=false（require默认值为true）或者没有添加@RequestParam注解时，则不会有这类问题
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseResult<String> missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        return ResponseResult.error(MessageFormat.format("缺少必要参数{0}", e.getParameterName()));
    }

    /**
     * HttpMessageNotReadableException：请求体的Json格式不正确导致无法正常读取，抛出此异常
     * <p>
     * 例：请求体的Json格式不符合规范
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseResult<String> httpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpMessageNotReadableException e) {
        log.error("_HttpMessageNotReadableException {} ：{}", request.getServletPath(), e.getMessage());
        return ResponseResult.error("请求体格式不正确");
    }

    /**
     * MethodArgumentTypeMismatchException：在解析请求方法的参数类型时，没有匹配到合适的转换器，抛出此异常
     * <p>
     * 例：比如接收参数必须是数字整型，结果传入了英文字符，导致无法正确转换；<br>
     * 再或者，接口参数为Date、LocalDateTime等日期对象，此时传入的日期格式和框架反序列化配置的格式不一致时，也会引发此异常
     * <p>
     * 注意：当控制层接口的方法参数在自定义的对象内时，该异常不会被抛出，<br>
     * 而是由BindException和MethodArgumentNotValidException取而代之
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseResult<String> methodArgumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException e) {
        return ResponseResult.error(MessageFormat.format("{0}的请求类型或格式不正确", e.getName()));
    }

    /**
     * DuplicateKeyException：当尝试插入或更新数据导致违反主键或唯一约束时引发异常，抛出此异常
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseResult<String> duplicateKeyExceptionHandler(HttpServletRequest request, DuplicateKeyException e) {
        log.error("_DuplicateKeyException {} ：{}", request.getServletPath(), e.getMessage());
        return ResponseResult.error("请求资源冲突，请检查后提交");
    }

    /**
     * SQLException：数据库访问错误或其他错误的信息的异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SQLException.class)
    public ResponseResult<String> sqlExceptionHandler(HttpServletRequest request, DuplicateKeyException e) {
        log.error("_SQLException {} ：{}", request.getServletPath(), e.getMessage());
        return ResponseResult.error();
    }

    /**
     * HttpRequestMethodNotSupportedException：当请求处理程序不支持特定请求方法时引发的异常。
     * <p>
     * 例：接口是post请求，请求时却使用的是get请求，抛出此异常
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseResult<String> httpRequestMethodNotSupportedExceptionHandle(HttpRequestMethodNotSupportedException e) {
        return ResponseResult.error(MessageFormat.format("请求方法{0}不支持", e.getMethod()));
    }

    /**
     * HttpMediaTypeNotSupportedException：当前接口不支持请求内容的类型时抛出异常
     * <p>
     * 例：
     */
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseResult<String> httpMediaTypeNotSupportedExceptionHandle(HttpMediaTypeNotSupportedException e) {
        return ResponseResult.error(e.getMessage());
    }

}