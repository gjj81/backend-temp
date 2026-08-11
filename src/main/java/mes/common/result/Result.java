package mes.common.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用响应结果封装
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 业务状态码 */
    private Integer code;

    /** 响应消息 */
    private String msg;

    /** 响应数据 */
    private T data;

    /** 服务器时间戳（毫秒） */
    private Long timestamp;

    /** 请求追踪ID（用于日志排查） */
    private String requestId;

    // ==================== 私有构造 ====================

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // ==================== 成功响应 ====================

    public static <T> Result<T> success() {
        return new Result<>(200, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功 + 自定义消息 */
    public static <T> Result<T> success(String msg) {
        return new Result<>(200, msg, null);
    }

    /** 成功 + 自定义消息 + 数据 */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    // ==================== 错误响应 ====================

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static <T> Result<T> badRequest(String msg) {
        return error(400, msg);
    }

    public static <T> Result<T> unauthorized(String msg) {
        return error(401, msg != null ? msg : "未授权");
    }

    public static <T> Result<T> forbidden(String msg) {
        return error(403, msg != null ? msg : "禁止访问");
    }

    public static <T> Result<T> notFound(String msg) {
        return error(404, msg != null ? msg : "资源不存在");
    }

    // ==================== 工具方法 ====================

    public boolean isSuccess() {
        return Integer.valueOf(200).equals(this.code);
    }
}