package mes.common.exception;

import lombok.extern.slf4j.Slf4j;
import mes.common.result.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public Result<?> handleBadCredentials(BadCredentialsException e) {
        return Result.error(401, "用户名或密码错误");
    }

    @ExceptionHandler(DisabledException.class)
    public Result<?> handleDisabled(DisabledException e) {
        return Result.error(401, "账号已被禁用");
    }

    @ExceptionHandler(AuthenticationException.class)
    public Result<?> handleAuthentication(AuthenticationException e) {
        return Result.error(401, "认证失败：" + e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDenied(AccessDeniedException e) {
        return Result.error(403, "无权限访问");
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public Result<?> handleMissingHeader(MissingRequestHeaderException e) {
        return Result.error(400, "缺少必要请求头: " + e.getHeaderName());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.error(400, msg);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleBadRequest(HttpMessageNotReadableException e) {
        return Result.error(400, "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}