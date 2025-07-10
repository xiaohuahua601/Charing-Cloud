package cn.tedu.charging.common.pojo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * 统一返回结果
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JsonResult<T> {

    /**
     * 状态码 2000表示成功 其他表示异常失败
     */
    Integer code;

    /**
     * 消息
     */
    String message;

    /**
     * 接口的出参,具体的数据
     */
    T data;


    public static JsonResult ok(Object data,String msg) {
        JsonResult result = new JsonResult<>();
        result.setCode(2000);
        result.setData(data);
        result.setMessage(msg);
        return result;
    }

    public static JsonResult ok(Object data) {
       return ok(data,null);
    }
}